package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义可预知异常
 * Created by 周大侠
 * 2019-04-01 14:49
 */
public class CustomException extends RuntimeException {
   private ResultCode resultCode;


    public CustomException(String message) {
        super(message);
    }

    public CustomException(ResultCode resultCode) {
        //异常信息为错误代码+异常信息        
        super("错误代码："+resultCode.code()+"错误信息："+resultCode.message());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}

