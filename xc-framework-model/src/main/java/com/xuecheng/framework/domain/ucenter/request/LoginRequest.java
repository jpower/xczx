package com.xuecheng.framework.domain.ucenter.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by admin on 2018/3/5.
 */
@Data
@ToString
public class LoginRequest extends RequestData {
    @NotEmpty(message = "用户名为空")
    String username;
    @NotEmpty(message = "密码为空")
    String password;
    String verifycode;

}
