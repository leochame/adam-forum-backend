package com.adam.service.impl;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.VedioConstants;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.request.UploadPartDTO;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.config.MinIOConfigProperties;
import com.adam.processors.VideoChunkProcessor;
import com.adam.service.VideoUploadService;
import com.adam.utils.VideoFileUtils;
import com.adam.vedio.constants.UploadStateManager;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class VideoUploadServiceImpl implements VideoUploadService {

    @Autowired
    private MinIOConfigProperties minIOConfigProperties;
    @Autowired
    private UploadStateManager stateManager;
    @Autowired
    private  VideoFileUtils fileUtils;
    @Autowired
    private  VideoChunkProcessor chunkProcessor;

    @Override
    @Transactional
    public BaseResponse<List<String>> uploadVideo(UploadPartDTO request) {
        String identifier = fileUtils.extractIdentifier(request.getResumableIdentifier());

        // 检查分片是否已上传
        if (stateManager.isChunkUploaded(identifier, request.getResumableChunkNumber())) {
            return ResultUtils.success(new ArrayList<>());
        }

        // 处理上传
        UploadResult result = processUpload(request, identifier);

        // 如果是最后一个分片，处理合并
        if (chunkProcessor.isLastChunk(request)) {
            return handleFinalChunk(request, identifier, result);
        }

        return ResultUtils.success(new ArrayList<>());
    }

    private UploadResult processUpload(UploadPartDTO request, String identifier) {
        AtomicReference<String> coverRef = new AtomicReference<>("");
        String fileName = fileUtils.generateUniqueFileName(identifier);

        // 处理封面
        if (chunkProcessor.shouldExtractCover(identifier)) {
            CompletableFuture<Void> coverTask = chunkProcessor.extractCover(
                    request, identifier, coverRef);
            stateManager.trackUploadTask(identifier, -1, coverTask);
        }

        // 上传分片
        CompletableFuture<Void> uploadTask = chunkProcessor.processChunk(
                request, identifier, fileName);
        stateManager.trackUploadTask(identifier, request.getResumableChunkNumber(), uploadTask);

        return new UploadResult(fileName, coverRef);
    }

    private BaseResponse<List<String>> handleFinalChunk(
            UploadPartDTO request, String identifier, UploadResult result)
    {
        try {

            stateManager.waitForAllTasks(identifier).join();
            if (stateManager.isAllPartsUploaded(identifier, request.getResumableTotalChunks())) {
                String mergedFileName = fileUtils.generateUniqueFileName(identifier);
                String finalUrl = fileUtils.generateFileUrl(
                        minIOConfigProperties.getReadPath(),
                        VedioConstants.VIDEO_BUCKET_NAME,
                        mergedFileName
                );

                chunkProcessor.mergeChunks(identifier, mergedFileName).join();
                stateManager.cleanup(identifier);

                List<String> results = new ArrayList<>();
                results.add(mergedFileName );

                return ResultUtils.success(results);
            }

        } catch (CompletionException e) {
            handleUploadError(e);
        }

        return ResultUtils.success(new ArrayList<>());
    }

    private void handleUploadError(CompletionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof BusinessException) {
            throw (BusinessException) cause;
        }
        throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, cause);
    }

    @Value
    private static class UploadResult {
        String fileName;
        AtomicReference<String> coverRef;
    }
}
