package com.adam.common.core.exception;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice  //控制器增强类
@Slf4j
public class ExceptionCatch {

    /**
     * 处理不可控异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse exception(Exception e){
        return ResultUtils.error(ErrorCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 处理可控异常  自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public BaseResponse exception(BusinessException e){
        return ResultUtils.error(e.getErrorCodeEnum());
    }
}