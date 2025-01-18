package com.adam.common.core.exception;


import com.adam.common.core.constant.ErrorCodeEnum;

/**
 * 抛异常工具类
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCodeEnum
     */
    public static void throwIf(boolean condition, ErrorCodeEnum errorCodeEnum) {
        throwIf(condition, new BusinessException(errorCodeEnum));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCodeEnum
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCodeEnum errorCodeEnum, String message) {
        throwIf(condition, new BusinessException(errorCodeEnum, message));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCodeEnum
     * @param message
     */
    public static void throwRpcIf(boolean condition, ErrorCodeEnum errorCodeEnum, String message) {
        throwIf(condition, new BusinessRpcException(errorCodeEnum, message));
    }
}
