package com.adam.common.core.response;

import com.adam.common.core.constant.ErrorCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回类
 *
 * @param <T>
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return Objects.equals(ErrorCodeEnum.SUCCESS.getCode(), this.code);
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), null, errorCodeEnum.getMessage());
    }
}
