package com.adam.common.core.exception;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  //控制器增强类
@Slf4j
public class ExceptionCatch {

    /**
     * 处理可控异常  自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> exception(BusinessException e){
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理不可控异常
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> exception(Exception e){
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCodeEnum.SYSTEM_ERROR);
    }

}