package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Created by 周大侠
 * 2019-04-14 10:40
 */
@ControllerAdvice
public class CustomExceptionCatch extends ExceptionCatch {
    static {
        //定义异常类型所对应的错误代码
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);

    }
}
