package com.adam.processors;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.request.UploadPartDTO;
import com.adam.service.FileStorageService;
import com.adam.vedio.constants.UploadPart;
import io.netty.handler.codec.EncoderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.ScreenExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.adam.common.core.constant.VedioConstants.VIDEO_TYPE_MP4;
import static com.adam.vedio.constants.UploadStateManager.uploadPartMap;

@Component
@Slf4j
public class VideoChunkProcessor {
    @Autowired
    private FileStorageService fileStorageService;
    
    public CompletableFuture<Void> processChunk(UploadPartDTO chunk, String identifier, String fileName) {
        return uploadPartAsync(chunk, identifier, fileName);
    }

    public CompletableFuture<Void> extractCover(UploadPartDTO chunk, String identifier, 
            AtomicReference<String> coverRef) {
        return extractCoverAsync(chunk, identifier, coverRef);
    }
    
    public CompletableFuture<Void> mergeChunks(String identifier, String mergedFileName) {
        return mergePartsAsync(identifier, mergedFileName);
    }



    // 判断是否为最后一个分片
    public boolean isLastChunk(UploadPartDTO uploadPartRequest) {
        return uploadPartRequest.getResumableChunkNumber().equals(uploadPartRequest.getResumableTotalChunks());
    }

    private CompletableFuture<Void> uploadPartAsync(UploadPartDTO request,
                                                    String identifier, String fileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                fileStorageService.uploadVideoPartFile(fileName,
                        request.getMultipartFile().getInputStream(),
                        VIDEO_TYPE_MP4);
                updatePartMap(identifier, request.getResumableChunkNumber(), fileName);
            } catch (Exception e) {
                log.error("分片上传失败: ", e);
                throw new BusinessException(ErrorCodeEnum.FILE_UPLOAD_ERROR);
            }
        });
    }

    private CompletableFuture<Void> mergePartsAsync(String identifier, String mergedFileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                fileStorageService.composePart(identifier, mergedFileName);
            } catch (Exception e) {
                log.error("分片合并失败: ", e);
                throw new BusinessException(ErrorCodeEnum.FILE_MERGE_ERROR);
            }
        });
    }


    private CompletableFuture<Void> extractCoverAsync(UploadPartDTO request,
                                                      String identifier, AtomicReference<String> coverRef) {
        return CompletableFuture.runAsync(() -> {
            Path tempDir = null;
            try {
                tempDir = Files.createTempDirectory("video-upload-");
                File videoFile = tempDir.resolve("video").toFile();
                //流式处理减少内存占用
                // Files.write(videoFile.toPath(), request.getMultipartFile().getBytes());
                try (InputStream videoInputStream = request.getMultipartFile().getInputStream()) {
                    Files.copy(videoInputStream, videoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                //提取封面图片
                File coverFile = tempDir.resolve(UUID.randomUUID().toString() + ".jpg").toFile();


                extractCoverImage(videoFile, coverFile);

                if (coverFile.exists()) {
                    try (InputStream is = new FileInputStream(coverFile)) {
                        String base64Cover = Base64.encode(IoUtil.readBytes(is));
                        coverRef.set(base64Cover);
                        updateUploadPartCoverMap(identifier, base64Cover);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to process video cover", e);
                throw new BusinessException(ErrorCodeEnum.VIDEO_PROCESS_ERROR, e);
            } finally {
                if (tempDir != null) {
                    try {
                        Files.walk(tempDir)
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                    } catch (IOException e) {
                        log.warn("Failed to cleanup temporary directory", e);
                    }
                }
            }
        });
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
     * 更新分片记录，将分片号与文件名关联，并更新已上传的分片数
     *
     * @param resumableIdentifier 分片上传的唯一标识符，用于标识当前上传任务
     * @param chunkNumber 当前上传的分片号
     * @param fileName 上传的文件名，表示该分片在存储中的名称
     */
    private void updatePartMap(String resumableIdentifier, Integer chunkNumber, String fileName) {
        // 获取当前上传任务的分片映射表，如果不存在则创建新映射表
        uploadPartMap.computeIfPresent(resumableIdentifier, (key, uploadPart) -> {
            uploadPart.getPartMap().put(chunkNumber, fileName);
            uploadPart.setTotalCount(uploadPart.getTotalCount() + 1);
            return uploadPart;
        });
    }

    public boolean shouldExtractCover(String identifier) {
        UploadPart uploadPart = uploadPartMap.computeIfAbsent(identifier,
                k -> new UploadPart());
        return uploadPart.getHasCutImg();
    }
}