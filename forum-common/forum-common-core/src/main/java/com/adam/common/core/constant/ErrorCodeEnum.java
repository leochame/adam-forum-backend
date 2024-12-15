package com.adam.common.core.constant;

/**
 * 自定义错误码
 */
public enum ErrorCodeEnum {

    SUCCESS(200, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NO_TOKEN_ERROR(40102, "token已过期"),
    USER_PASSWORD_ERROR(40200, "用户密码错误"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    TOO_MANY_REQUEST(42900, "请求过于频繁"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    EXCEPTION_ERROR(50002, "服务器出了点小差"),
    //MinIO服务内部错误
    MinIO_SERVER_ERROR(50100, "MinIO 服务器错误"),
    DATA_INSUFFICIENT(50200, "数据不足"),
    MinIO_ERROR_RESPONSE(50300, "MinIO 错误响应"),
    IO_ERROR(50400, "I/O 错误"),
    INVALID_KEY_ERROR(50500, "无效密钥错误"),
    INVALID_RESPONSE_ERROR(50600, "无效响应格式"),
    XML_PARSE_ERROR(50700, "XML 解析错误"),
    MinIO_INTERNAL_CLIENT_ERROR(50800, "MinIO 客户端内部错误");;


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
