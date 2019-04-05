package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * Created by 周大侠
 * 2019-04-01 14:56
 */
public class ExceptionCast {

    /**
     * 使用静态方法抛出自定义异常
     * @param resultCode
     */
    public static void exception(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
