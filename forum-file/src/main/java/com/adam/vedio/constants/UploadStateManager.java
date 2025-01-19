package com.adam.vedio.constants;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类用于存储与视频分片上传相关的常量和共享数据。
 */
@Component
public class UploadStateManager {

    /**
     * 使用 `ConcurrentHashMap` 以保证线程安全。
     * <p>
     * 这个 `ConcurrentHashMap` 用于存储每个上传任务的分片信息，键是分片标识符（`presumableIdentifier`），
     * 值是 `UploadPart` 对象，该对象包含分片上传的状态和其他相关数据。
     * </p>
     */
    public static Map<String, UploadPart> uploadPartMap = new ConcurrentHashMap<>();

    public static Map<String, Map<Integer, CompletableFuture<Void>>> uploadTasksMap = new ConcurrentHashMap<>();

    public boolean isChunkUploaded(String identifier, Integer chunkNumber) {
        return Optional.ofNullable(uploadPartMap.get(identifier))
                .map(part -> part.getPartMap().containsKey(chunkNumber))
                .orElse(false);
    }

    public void trackUploadTask(String identifier, Integer chunkNumber,
                                CompletableFuture<Void> task) {
        uploadTasksMap.computeIfAbsent(identifier, k -> new ConcurrentHashMap<>())
                .put(chunkNumber, task);
    }

    public CompletableFuture<Void> waitForAllTasks(String identifier) {
        Map<Integer, CompletableFuture<Void>> tasks = uploadTasksMap.get(identifier);
        return CompletableFuture.allOf(tasks.values().toArray(new CompletableFuture[0]));
    }

    public boolean isAllPartsUploaded(String identifier, Integer totalChunks) {
        UploadPart uploadPart = uploadPartMap.get(identifier);
        return uploadPart != null && uploadPart.getTotalCount().equals(totalChunks);
    }

    public void cleanup(String identifier) {
        uploadPartMap.remove(identifier);
    }
}