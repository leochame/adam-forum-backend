package com.admin.video.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadPartDTO {

    /**
     * 当前上传的文件分片。
     * 通过 MultipartFile 接口处理文件上传。
     */
    MultipartFile multipartFile;

    /**
     * 总的文件分片数量，用于标识该文件被分成了多少个分片。
     * 例如，如果文件被分成 10 个分片上传，则此值为 10。
     */
    Integer resumableTotalChunks;

    /**
     * 文件上传过程中的唯一标识符，确保可以识别同一个文件的不同分片。
     * 通常由前端生成并传递，用于在分片上传过程中保持文件一致性。
     */
    String resumableIdentifier;

    /**
     * 当前上传的分片编号。
     * 用于标识这是该文件的第几个分片，例如第 1 个分片，或第 2 个分片等。
     */
    Integer resumableChunkNumber;
}
