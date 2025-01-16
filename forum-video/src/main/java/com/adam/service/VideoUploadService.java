package com.adam.service;

import com.adam.common.core.response.BaseResponse;
import com.admin.video.dtos.UploadPartDTO;

import java.util.List;

public interface VideoUploadService {
    BaseResponse<List<String>> uploadVideo(UploadPartDTO uploadPartDTO);
}
