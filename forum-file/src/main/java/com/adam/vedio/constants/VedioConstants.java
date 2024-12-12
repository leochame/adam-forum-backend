package com.adam.vedio.constants;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类用于存储与视频分片上传相关的常量和共享数据。
 */
@Component
public class VedioConstants {

    /**
     * 使用 `ConcurrentHashMap` 以保证线程安全。
     * <p>
     * 这个 `ConcurrentHashMap` 用于存储每个上传任务的分片信息，键是分片标识符（`presumableIdentifier`），
     * 值是 `UploadPart` 对象，该对象包含分片上传的状态和其他相关数据。
     * </p>
     */
    public static Map<String, UploadPart> uploadPartMap = new ConcurrentHashMap<>();
}