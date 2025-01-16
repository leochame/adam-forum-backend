package com.adam.service.impl;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.config.MinIOConfig;
import com.adam.config.MinIOConfigProperties;
import com.adam.service.FileStorageService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static com.adam.vedio.constants.VedioConstants.uploadPartMap;


@Slf4j
@EnableConfigurationProperties(MinIOConfigProperties.class)
@Import(MinIOConfig.class)
@Component
public class MinIOFileStorageService implements FileStorageService {

    private static final String VIDEO_BUCKET_NAME = "video";

    private final MinioClient minioClient;
    private final MinIOConfigProperties minIOConfigProperties;

    private final static String separator = "/";

    public MinIOFileStorageService(MinioClient minioClient, MinIOConfigProperties minIOConfigProperties) {
        this.minioClient = minioClient;
        this.minIOConfigProperties = minIOConfigProperties;
    }


    public String builderFilePath(String dirPath,String filename) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if(dirPath != null) {
            stringBuilder.append(dirPath).append(separator);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(separator);
        stringBuilder.append(filename);
        return stringBuilder.toString();
    }

    /**
     *  上传图片文件
     * @param prefix  文件前缀
     * @param filename  文件名
     * @param inputStream 文件流
     * @return  文件全路径
     */
    @Override
    public String uploadImgFile(String prefix, String filename,InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("image/jpg")
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            // 返回文件URL
            return minIOConfigProperties.getReadPath() + "/" + minIOConfigProperties.getBucket() + "/" + filePath;
        }catch (Exception ex){
            log.error("minio put file error.",ex);
            throw new RuntimeException("上传文件失败");
        }
    }



    /**
     *  上传html文件
     * @param prefix  文件前缀
     * @param filename   文件名
     * @param inputStream  文件流
     * @return  文件全路径
     */
    @Override
    public String uploadHtmlFile(String prefix, String filename,InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("text/html")
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            //返回文件url路径
            return minIOConfigProperties.getReadPath() + separator + minIOConfigProperties.getBucket() +
                    separator +
                    filePath;
        } catch (ServerException e) {
            // 处理 MinIO 服务器端的异常
            log.error("MinIO 服务器错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.MinIO_SERVER_ERROR, e.getMessage());
        } catch (InsufficientDataException e) {
            // 数据不足，可能需要重试
            log.warn("数据不足错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.DATA_INSUFFICIENT, e.getMessage());
        } catch (ErrorResponseException e) {
            // 提取具体的错误码和信息，向用户反馈
            log.error("MinIO 响应错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.MinIO_ERROR_RESPONSE, e.getMessage());
        } catch (IOException e) {
            // 网络或 I/O 问题
            log.error("I/O 异常: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.IO_ERROR, e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // 加密配置问题
            log.error("加密配置错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.INVALID_KEY_ERROR, e.getMessage());
        } catch (InvalidResponseException e) {
            // 无效的响应
            log.error("无效响应: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.INVALID_RESPONSE_ERROR, e.getMessage());
        } catch (XmlParserException e) {
            // XML 格式问题
            log.error("XML 解析错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.XML_PARSE_ERROR, e.getMessage());
        } catch (InternalException e) {
            // MinIO 内部问题
            log.error("MinIO 客户端内部错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.MinIO_INTERNAL_CLIENT_ERROR, e.getMessage());
        }

    }


    /**
     *  上传视频文件
     * @param prefix  文件前缀
     * @param filename  文件名
     * @param inputStream 文件流
     * @return  文件全路径
     */
    @Override
    public String uploadVideoFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            // 这里设置为视频文件的 MIME 类型，如 MP4 格式为 video/mp4
            String contentType = "video/mp4";

            // 构建 PutObjectArgs 对象，设置文件路径、内容类型、桶名、文件流等
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType(contentType)
                    .bucket(minIOConfigProperties.getBucket())
                    .stream(inputStream, inputStream.available(), -1)
                    .build();

            // 执行上传
            minioClient.putObject(putObjectArgs);

            // 构建返回的 URL，包含存储路径
            StringBuilder urlPath = new StringBuilder(minIOConfigProperties.getReadPath());
            urlPath.append(separator).append(minIOConfigProperties.getBucket());
            urlPath.append(separator).append(filePath);

            return urlPath.toString();
        } catch (Exception ex) {
            log.error("minio put file error.", ex);
            throw new RuntimeException("上传视频文件失败");
        }
    }



    @Override
    public Boolean uploadVideoPartFile(String fileName, InputStream stream, String contentType) {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            System.out.println("Buckets available:");
            for (Bucket bucket : buckets) {
                System.out.println(bucket.name());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        // 使用 try-with-resources 语法自动关闭 InputStream，确保资源不会泄漏
        try (stream) {
            // 通过 MinIO 客户端上传文件
            // - bucket: 指定存储桶名称
            // - object: 文件在 MinIO 中存储的名称（包括路径）
            // - stream: 文件内容的输入流
            // - -1 表示未知的文件长度，10485760 表示 10MB 的最大内存缓冲区大小
            // - contentType: 文件的 MIME 类型，用于识别文件格式
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(VIDEO_BUCKET_NAME)  // 存储桶名称
                    .object(fileName)  // 文件名
                    .stream(stream, -1, 10485760)  // 输入流、文件长度（未知）、最大内存缓冲区大小
//                    .contentType(contentType)  // MIME 类型
                    .build();
            minioClient.putObject(putObjectArgs);
            // 文件上传成功，返回 true
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            // 捕获并处理 IO 异常，如在关闭输入流时发生错误
            throw new RuntimeException("关闭输入流时出错", e);
        } catch (MinioException e) {
            e.printStackTrace();
            // 捕获并处理 MinIO 相关的异常，如上传过程中发生错误
            throw new RuntimeException("MinIO 上传失败", e);
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获并处理所有其他类型的异常，提供上传失败的提示
            throw new RuntimeException("上传失败", e);
        }
    }



    /**
     * 合成分片
     *
     * @param resumableIdentifier 分片上传的唯一标识符
     * @param name                合成后的视频文件名
     * @return 成功时返回true
     * @throws ServerException           如果MinIO服务器返回错误响应
     * @throws InsufficientDataException 如果在读取流时数据不足
     * @throws ErrorResponseException    如果MinIO返回错误响应
     * @throws IOException               如果发生IO错误
     * @throws NoSuchAlgorithmException  如果指定的算法不存在
     * @throws InvalidKeyException       如果访问密钥无效
     * @throws InvalidResponseException  如果MinIO返回无效响应
     * @throws XmlParserException        如果XML解析出错
     * @throws InternalException         如果MinIO SDK内部发生错误
     */
    @Override
    public String composePart(String resumableIdentifier, String name) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 创建存储合并分片信息的列表
        List<ComposeSource> composeSourceList = new ArrayList<>();
        // 创建需要删除的分片文件名列表
        List<String> deleteObjects = new ArrayList<>();

        // 获取分片信息并封装成ComposeSource类的集合，确保按分片顺序排序
        List<Map.Entry<Integer, String>> sortedParts = new ArrayList<>(uploadPartMap.get(resumableIdentifier).getPartMap().entrySet());
        sortedParts.sort(Map.Entry.comparingByKey()); // 按照分片序号排序

        for (Map.Entry<Integer, String> entry : sortedParts) {
            // 记录日志，确保每个分片的顺序正确
            System.out.println("Adding part " + entry.getKey() + " with object name: " + entry.getValue());
            composeSourceList.add(ComposeSource.builder().bucket(VIDEO_BUCKET_NAME).object(entry.getValue()).build());
            deleteObjects.add(entry.getValue());
        }

        // 合并操作
        String outputFileName = name + ".mp4"; // 确保输出文件名带有 .mp4 扩展名
        minioClient.composeObject(ComposeObjectArgs.builder().sources(composeSourceList).object(outputFileName).bucket(VIDEO_BUCKET_NAME).build());

        // 合并后检查文件内容
        System.out.println("File " + outputFileName + " composed successfully.");

        // 确保合并完成后再执行删除操作
        CompletableFuture.runAsync(() -> {
            for (String objectName : deleteObjects) {
                try {
                    System.out.println("Deleting part: " + objectName);
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(VIDEO_BUCKET_NAME).object(objectName).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 返回合并后的文件路径
        return outputFileName; // 可以返回文件名或者URL
    }


    private StringBuffer generateFileUrl(String readPath,String bucket,String name){
        StringBuffer stringBuffer = new StringBuffer().append(readPath).append(bucket).append(name);
        return stringBuffer;
    }


    /**
     * 删除文件
     *
     * @param pathUrl 文件全路径
     */
    @Override
    public void delete(String pathUrl) {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint()+"/","");
        int index = key.indexOf(separator);
        String bucket = key.substring(0,index);
        String filePath = key.substring(index+1);
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio remove file error.  pathUrl:{}",pathUrl);
            e.printStackTrace();
        }
        return;
    }


    /**
     * 下载文件
     * @param pathUrl  文件全路径
     * @return  文件流
     *
     */
    @Override
    public byte[] downLoadFile(String pathUrl)  {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint()+"/","");
        int index = key.indexOf(separator);
        String bucket = key.substring(0,index);
        String filePath = key.substring(index+1);
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minIOConfigProperties.getBucket()).object(filePath).build());
        } catch (Exception e) {
            log.error("minio down file error.  pathUrl:{}",pathUrl);
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while (true) {
            try {
                if (!((rc = inputStream.read(buff, 0, 100)) > 0)) {break;}
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}