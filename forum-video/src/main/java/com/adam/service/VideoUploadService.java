package com.adam.service;

import com.adam.common.core.request.UploadPartDTO;
import com.adam.common.core.response.BaseResponse;

import java.util.List;

public interface VideoUploadService {
    BaseResponse<List<String>> uploadVideo(UploadPartDTO uploadPartDTO);
}
