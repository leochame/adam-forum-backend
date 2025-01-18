package com.adam.common.core.exception;

import com.adam.common.core.constant.ErrorCodeEnum;
import org.apache.dubbo.rpc.RpcException;

/**
 * Rpc 调用异常类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/18 20:30
 */
public class BusinessRpcException extends RpcException {


    /**
     * 错误码枚举
     */
    private ErrorCodeEnum errorCodeEnum;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 错误码
     */
    private int errorCode;

    /**
     * 构造函数：使用枚举类型初始化错误码和消息
     */
    public BusinessRpcException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());  // 调用父类构造器设置异常消息
        this.errorCodeEnum = errorCodeEnum;
        this.errorCode = errorCodeEnum.getCode();
        this.errorMessage = errorCodeEnum.getMessage();
    }

    /**
     * 构造函数：使用枚举类型初始化错误码和消息
     * 并且可以传递自定义消息
     */
    public BusinessRpcException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);  // 使用自定义消息
        this.errorCodeEnum = errorCodeEnum;
        this.errorCode = errorCodeEnum.getCode();
        this.errorMessage = message;
    }

    /**
     * 构造函数：使用枚举类型初始化错误码、消息和异常原因
     * 此时可以提供堆栈信息
     */
    public BusinessRpcException(ErrorCodeEnum errorCodeEnum, String message, Throwable cause) {
        super(message, cause);  // 调用父类构造器设置异常消息和原因
        this.errorCodeEnum = errorCodeEnum;
        this.errorCode = errorCodeEnum.getCode();
        this.errorMessage = message;
    }

    /**
     * 构造函数：不提供自定义消息，使用默认消息
     * 传递异常原因
     */
    public BusinessRpcException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getMessage(), cause);
        this.errorCodeEnum = errorCodeEnum;
        this.errorCode = errorCodeEnum.getCode();
        this.errorMessage = errorCodeEnum.getMessage();
    }

    public int getCode() {
        return errorCode;
    }
}
