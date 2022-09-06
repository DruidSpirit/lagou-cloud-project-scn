package com.lagou.edu.fallback;

import com.lagou.edu.SendEmail;
import org.springframework.stereotype.Component;

@Component
public class SendEmailFallback implements SendEmail {

    @Override
    public boolean sendVerificationCode(String verificationCode,String email) {
        System.out.println("验证码邮箱发送服务器熔断");
        return false;
    }
}
