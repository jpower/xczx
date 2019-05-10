package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常捕获类
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:32
 **/
@ControllerAdvice//控制器增强
@Slf4j
public class ExceptionCatch {


    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }



    /**
     * 捕获CustomException此类异常
     * @param customException
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        customException.printStackTrace();
        //记录日志
        log.error("catch exception:{}",customException.getMessage());
        ResultCode resultCode = customException.getResultCode();

        if(resultCode != null) {
            return new ResponseResult(resultCode);
        }else {
            return new ResponseResult(false,400,customException.getMessage());
        }
    }

    /**
     * 捕获Exception此类异常
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        exception.printStackTrace();
        //记录日志
        log.error("catch exception:{}",exception.getMessage());
        //定义map，配置异常类型所对应的错误代码
        ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS = builder.build();
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if(resultCode == null) {
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
        return new ResponseResult(resultCode);


    }


}
