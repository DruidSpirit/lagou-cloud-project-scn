package com.lagou.edu.control;

import com.lagou.edu.util.EmailUtil;
import com.lagou.edu.SendEmail;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("email")
@RestController
@AllArgsConstructor
public class SendEmailControl implements SendEmail {

    private final EmailUtil emailUtil;

    /**
     * 通过邮箱发送验证码
     *
     * @param verificationCode 被发送的验证码
     * @return true 发送成功 false 发送失败
     */
    @PostMapping("sendVerificationCode")
    @Override
    public boolean sendVerificationCode(@RequestParam("verificationCode") String verificationCode, @RequestParam("email")String email) {

        try {

            //  发送邮箱
            emailUtil.sendSimpleMail(email,"注册验证码","你好，你的注册验证码是:"+verificationCode);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("邮件发送异常");
            return false;
        }
        System.out.println("验证码通知邮件发送成功！");
        return true;
    }
}
