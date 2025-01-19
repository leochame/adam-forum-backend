package com.adam.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VideoFileUtils {

    public String generateUniqueFileName(String baseName) {
        return baseName + UUID.randomUUID().toString().substring(0, 10);
    }
    
    public String generateFileUrl(String readPath, String bucket, String name) {
        return String.join("/", readPath, bucket, name);
    }
    
    public String extractIdentifier(String identifier) {
        return identifier.substring(0, identifier.indexOf(','));
    }
}