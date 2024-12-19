package com.adam.common.auth.config;

import java.util.Arrays;
import java.util.List;

/**
 * fa
 */
public class PathPatterns {


    /**
     * 需要过滤
     */
    public static final List<String> PATH_PATTERNS = Arrays.asList(
            "/"
    );

    /**
     * 无需过滤路径
     */
    public static final List<String> EXCLUDE_PATH_PATTERNS = Arrays.asList(
            "/auth/login/**",
            "/v3/api-docs",
            "/doc.html"
    );

}
