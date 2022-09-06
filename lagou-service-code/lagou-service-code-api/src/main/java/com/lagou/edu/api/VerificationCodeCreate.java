package com.lagou.edu.api;

import com.lagou.edu.ResModel;
import com.lagou.edu.fallback.VerificationCodeCreateFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "lagou-service-code",fallback = VerificationCodeCreateFallback.class,path = "/verificationCode")
public interface VerificationCodeCreate {

    /**
     * 生产验证码并通过邮箱发送
     * @return  验证码
     */
    @GetMapping("createCodeAndSendEmail")
    ResModel createCodeAndSendEmail( @PathVariable String email );
}
