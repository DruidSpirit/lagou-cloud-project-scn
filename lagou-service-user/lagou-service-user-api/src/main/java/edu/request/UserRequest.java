package edu.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserRequest {

    /**
     * 用户邮箱
     */
    @Email
    private String email;

    /**
     * 用户密码
     */
    @Length(min = 6,max = 11,message = "用户密码长度要求6～11个字符")
    @NotNull
    private String password;

    /**
     * 邮箱验证码
     */
    @Length(min = 6,max = 6,message = "验证码长度为6")
    @NotNull
    private String code;

}
