package com.adam.common.core.exception;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice  //控制器增强类
@Slf4j
public class ExceptionCatch {

    /**
     * 处理可控异常  自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> exception(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 rpc 调用异常
     */
    @ExceptionHandler(RpcException.class)
    public BaseResponse<?> exception(BusinessRpcException e) {
        log.error("BusinessRpcException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理不可控异常
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> exception(Exception e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 处理参数校验异常
     *
     * @return 返回第一处出现的参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        Collection<String> values = errorMap.values();
        String errorMessage = values.stream().findFirst().orElse("参数错误");
        return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, errorMessage);
    }
}