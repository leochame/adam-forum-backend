package com.adam.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.config.MinIOConfigProperties;
import com.adam.service.FileStorageService;
import com.adam.service.VideoUploadService;
import com.adam.vedio.constants.UploadPart;
import com.admin.video.constants.VedioConstants;
import com.admin.video.dtos.UploadPartDTO;
import io.minio.errors.*;
import io.netty.handler.codec.EncoderException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.ScreenExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import static com.adam.vedio.constants.VedioConstants.uploadPartMap;
import static com.admin.video.constants.VedioConstants.VIDEO_TYPE_MP4;

@Service
@Slf4j
public class VideoUploadServiceImpl implements VideoUploadService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private MinIOConfigProperties minIOConfigProperties;

    @SneakyThrows
    @Override
    @Transactional
    public BaseResponse<List<String>> uploadVideo(UploadPartDTO uploadPartRequest){
        // 提取分片标识符，通常用于唯一标识该文件上传任务
        String resumableIdentifier = extractIdentifier(uploadPartRequest.getResumableIdentifier());
        String fileUrl = null;
        AtomicReference<String> videoCover = new AtomicReference<>("");
        CompletableFuture<Void> coverFuture = null;
        CompletableFuture<Void> composeFuture = null;
// 检查是否已经提取过封面图，若没有则进行封面图提取
        if (uploadPartMap.get(resumableIdentifier) == null || !uploadPartMap.get(resumableIdentifier).getHasCutImg()) {
            // 封面提取异步化处理
            coverFuture = CompletableFuture.runAsync(() -> {
                try (InputStream videoFileInputStream = uploadPartRequest.getMultipartFile().getInputStream()) {
                    // 将视频文件读入字节数组
                    byte[] bytes = IoUtil.readBytes(videoFileInputStream);

                    // 创建临时目录，用于存放视频文件和封面图
                    String filePath = Files.createTempDirectory(".tmp").toString();
                    String coverFileName = UUID.randomUUID().toString().substring(0, 10) + ".jpg"; // 生成随机文件名
                    String videoFileName = "video"; // 视频文件的默认名称
                    File directory = new File(filePath); // 临时目录
                    File videoFile = new File(filePath, videoFileName); // 视频文件路径
                    File coverFile = new File(directory, coverFileName); // 封面图文件路径

                    // 将视频文件写入临时目录
                    Files.write(Paths.get(videoFile.getAbsolutePath()), bytes);

                    // 提取视频封面图像
                    extractCoverImage(videoFile, coverFile);

                    // 如果封面图像文件存在
                    if (coverFile.exists()) {
                        // 读取封面图像文件并转为Base64编码
                        try (InputStream inputStream = new FileInputStream(coverFile)) {
                            videoCover.set(Base64.encode(IoUtil.readBytes(inputStream))); // 直接赋值操作

                        }

                        // 删除临时生成的视频文件和封面图像文件
                        videoFile.delete();
                        coverFile.delete();

                        // 更新分片上传状态，标识封面图已经提取并保存封面图信息
                        updateUploadPartCoverMap(resumableIdentifier, videoCover.get());
                    }
                } catch (IOException | EncoderException e) {
                    // 异常处理，例如日志记录
                    e.printStackTrace();
                }
            });
        }


        // 生成唯一文件名
        String name = generateUniqueFileName(resumableIdentifier);
        // 上传分片到MinIO
        fileStorageService.uploadVideoPartFile(name, uploadPartRequest.getMultipartFile().getInputStream(), VIDEO_TYPE_MP4);

        // 更新上传的分片信息
        updatePartMap(resumableIdentifier, uploadPartRequest.getResumableChunkNumber(), name);

        // 如果所有分片已上传完毕，进行文件合并

// 如果所有分片已上传完毕，进行文件合并
        if (isAllPartsUploaded(resumableIdentifier, uploadPartRequest.getResumableTotalChunks())) {
            String filePathName = generateUniqueFileName(resumableIdentifier);
            fileUrl = String.valueOf(generateFileUrl(minIOConfigProperties.getReadPath(), VedioConstants.VIDEO_BUCKET_NAME,filePathName));

            String finalFileUrl = fileUrl;

            composeFuture = CompletableFuture.runAsync(() -> {
                // 合并分片

                try {
                    fileStorageService.composePart(resumableIdentifier, filePathName);
                } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                         NoSuchAlgorithmException |
                         InvalidKeyException | InvalidResponseException | XmlParserException |
                         InternalException e) {
                    // 处理异常，例如记录日志或重新抛出异常
                    e.printStackTrace();
                    throw new RuntimeException("Failed to compose parts", e);
                }

                // 保存到数据库
//                StudioMaterial studioMaterial = StudioMaterial.builder()
//                        .url(finalFileUrl)
//                        .type((short) 1)
//                        .userId(UserThreadLocalUtil.getCurrentId())
//                        .createdTime(new Date())
//                        .isCollection((short) 0)
//                        .build();
//                save(studioMaterial);
            }).exceptionally(ex -> {
                // 异常处理，例如记录日志
                ex.printStackTrace();
                return null;
            });
        }

        // 等待封面提取完成（如果存在这些操作）
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                coverFuture != null ? coverFuture : CompletableFuture.completedFuture(null)
        );

        // 创建一个字符串列表用于保存合并后的结果
        List<String> list = new ArrayList<>();

        try {
            allOf.join(); // 等待所有操作完成

            // 添加合并后的视频名称到列表
            list.add(fileUrl);

            // 添加视频封面图的 Base64 编码字符串到列表
            list.add(videoCover.get());

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ErrorCodeEnum.EXCEPTION_ERROR);
        }

        return ResultUtils.success(list);

    }

    /**
     * 生成完整的文件URL。
     *
     * @param readPath 基础路径，通常是服务器地址或根路径
     * @param bucket   存储桶名称或其他路径标识符
     * @param name     文件名
     * @return 完整的文件URL
     */
    private String generateFileUrl(String readPath, String bucket, String name) {
        // 创建Path对象，确保路径格式正确
        String url = String.join("/", readPath, bucket, name);

        // 返回构建好的URL字符串
        return url;
    }

    /**
     * 提取标识符中的前缀部分。
     *
     * @param identifier 完整的标识符，假设格式为"前缀,后缀"。
     * @return 标识符中的前缀部分，即逗号之前的部分。
     */
    private String extractIdentifier(String identifier) {
        // 查找标识符中逗号的位置
        int commaIndex = identifier.indexOf(',');

        // 返回标识符中逗号之前的部分，作为前缀部分
        return identifier.substring(0, commaIndex);
    }



    /**
     * 从视频文件中提取第一帧作为封面图，并将其保存到指定的文件路径中
     * @param videoFile
     * @param coverFile
     * @throws EncoderException
     */
    private void extractCoverImage(File videoFile, File coverFile) throws EncoderException {
        // 创建ScreenExtractor对象，用于从视频中提取帧
        ScreenExtractor screenExtractor = new ScreenExtractor();

        // 创建MultimediaObject对象，表示要处理的视频文件
        MultimediaObject multimediaObject = new MultimediaObject(videoFile);

        // 从视频中提取第一帧图像并保存到coverFile指定的位置
        // 参数解析：
        // -1, -1 表示默认的宽度和高度
        // 1000 表示截取的视频时间点（以毫秒为单位），这里是1秒
        // coverFile 是封面图的保存路径
        // 1 表示提取第1帧图像
        try {
            screenExtractor.renderOneImage(multimediaObject, -1, -1, 1000, coverFile, 1);
        } catch (ws.schild.jave.EncoderException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 更新分片上传状态，记录封面图和封面提取状态。
     *
     * @param resumableIdentifier 文件上传的唯一标识符，用于在上传过程中标识一个完整文件。
     * @param cover               封面图的 Base64 编码字符串，用于显示视频的第一帧图像。
     */
    private void updateUploadPartCoverMap(String resumableIdentifier, String cover) {
        // 根据唯一标识符从 map 中获取对应的 UploadPart 对象，若不存在则创建新的 UploadPart 对象
        UploadPart uploadPart = uploadPartMap.getOrDefault(resumableIdentifier, new UploadPart());

        // 设置已提取封面图的标志为 true，表明封面图已提取
        uploadPart.setHasCutImg(true);

        // 将封面图的 Base64 编码字符串存储到 UploadPart 对象中
        uploadPart.setCover(cover);

        // 将更新后的 UploadPart 对象重新放回 map 中，确保更新分片上传状态
        uploadPartMap.put(resumableIdentifier, uploadPart);
    }


    /**
     * 生成唯一文件名
     *
     * @param baseName 基础文件名，用于文件命名的前缀。
     * @return 返回带有随机后缀的唯一文件名，后缀由 UUID 生成并截取前 10 位。
     */
    private String generateUniqueFileName(String baseName) {
        // 使用 UUID 生成随机字符串，并截取前 10 位作为文件名后缀，与基础文件名前缀拼接
        return baseName + UUID.randomUUID().toString().substring(0, 10);
    }


    /**
     * 更新分片记录，将分片号与文件名关联，并更新已上传的分片数
     *
     * @param resumableIdentifier 分片上传的唯一标识符，用于标识当前上传任务
     * @param chunkNumber 当前上传的分片号
     * @param fileName 上传的文件名，表示该分片在存储中的名称
     */
    private void updatePartMap(String resumableIdentifier, Integer chunkNumber, String fileName) {
        // 获取当前上传任务的分片映射表，如果不存在则创建新的映射表
        Map<Integer, String> newUploadPartMap = uploadPartMap.getOrDefault(resumableIdentifier, new UploadPart()).getPartMap();
        // 将当前分片号和对应的文件名添加到映射表中
        newUploadPartMap.put(chunkNumber, fileName);
        // 获取当前上传任务的 UploadPart 对象，如果不存在则创建新的对象
        UploadPart uploadPart = uploadPartMap.getOrDefault(resumableIdentifier, new UploadPart());
        // 更新 UploadPart 对象中的分片映射表
        uploadPart.setPartMap(newUploadPartMap);
        // 将更新后的 UploadPart 对象放回到全局的上传任务映射表中
        uploadPartMap.put(resumableIdentifier, uploadPart);
        // 更新该上传任务的已上传分片数
        uploadPartMap.get(resumableIdentifier).setTotalCount(uploadPartMap.get(resumableIdentifier).getTotalCount() + 1);
    }

    /**
     * 判断是否所有分片都已经上传完成
     *
     * @param resumableIdentifier 唯一标识上传任务的标识符
     * @param totalChunks          总分片数量
     * @return 如果已上传的分片数量等于总分片数量，返回 true；否则返回 false
     */
    private boolean isAllPartsUploaded(String resumableIdentifier, Integer totalChunks) {
        // 获取当前上传任务的分片上传记录，比较已上传的分片数量和总分片数量是否相等
        UploadPart uploadPart = uploadPartMap.get(resumableIdentifier);
        return uploadPartMap.get(resumableIdentifier).getTotalCount().equals(uploadPart.getTotalCount());
    }

}
