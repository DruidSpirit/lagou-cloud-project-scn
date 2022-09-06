package com.lagou.edu;

import com.lagou.edu.fallback.SendEmailFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "lagou-service-email",fallback = SendEmailFallback.class,path = "/email")
public interface SendEmail {

    /**
     * 通过邮箱发送验证码
     * @param verificationCode  被发送的验证码
     * @return                  true 发送成功 false 发送失败
     */
    @PostMapping("sendVerificationCode")
    boolean sendVerificationCode (@RequestParam("verificationCode") String verificationCode, @RequestParam("email")String email);
}
