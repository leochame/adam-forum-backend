import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class VideoUploadTest {

    private static final String UPLOAD_URL = "http://localhost:51803/api/v1/video/uploadPart";
    private static final int CHUNK_SIZE = 1024 * 1024 * 5; // 5MB
    private static final String TOKEN ="eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_zWLSQrDMAwA_6JzDPFW2fmNhAV1SKlBDrSU_r3KobcZhvnAPjtswCSREdGtjbNLQZJjqs1lCYljqR6jhwU6Tdg8hlRLiLeygJ5st751yuPqqqZ3OY6nGZ3NjMYwltf4n7leZ7fmvz8yOcszgAAAAA.LcUkHsbylfTYFOk2HA0CbpM6xCWkbl-dE9mIc7emfSOwZFISEm62sYKvqNijZgYGmdBzXRRor028YNO1ZYGgHA";
    @Test
    public void testChunkedUpload() throws IOException {
        String filePath = "/home/liulch/123.mp4"; // 视频文件路径
        String resumableIdentifier = "testqwd,";
        String resumableFilename = "video.mp4";
        int totalChunks = (int) Math.ceil(new FileInputStream(filePath).available() / (double) CHUNK_SIZE);

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int chunkNumber = 1;
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] chunkData = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunkData, 0, bytesRead);

                // 创建MockMultipartFile
                MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", resumableFilename,
                        MediaType.MULTIPART_FORM_DATA_VALUE, chunkData);

                // 创建MultiValueMap来封装文件和其他数据
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("multipartFile", new ByteArrayResource(multipartFile.getBytes()) {
                    @Override
                    public String getFilename() {
                        return multipartFile.getOriginalFilename();
                    }
                });
                body.add("resumableTotalChunks", totalChunks);
                body.add("resumableIdentifier", resumableIdentifier);
                body.add("resumableChunkNumber", chunkNumber);

                // 创建请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                headers.set("token",TOKEN);
// 使用Bearer Token进行认证

                // 构造请求体
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // 发送分片请求
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_URL, requestEntity, String.class);

                System.out.println("Chunk " + chunkNumber + " uploaded, response: " + response.getBody());

                chunkNumber++;
            }
        }
    }
}