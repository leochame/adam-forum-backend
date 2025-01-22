package com.adam.controller;

import com.adam.common.core.request.UploadPartDTO;
import com.adam.common.core.response.BaseResponse;
import com.adam.service.VideoUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/video")
@Slf4j
public class VideoUploadController {
    @Autowired
    private VideoUploadService videoUploadService;

    @PostMapping("/uploadPart")
    public BaseResponse<List<String>> uploadPart(@ModelAttribute UploadPartDTO uploadPartDTO) {
        return videoUploadService.uploadVideo(uploadPartDTO);
    }
}
