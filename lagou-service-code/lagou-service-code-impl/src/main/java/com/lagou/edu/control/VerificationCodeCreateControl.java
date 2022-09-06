package com.lagou.edu.control;

import com.lagou.edu.dao.LagouAuthCodeDao;
import com.lagou.edu.ResModel;
import com.lagou.edu.SendEmail;
import com.lagou.edu.api.VerificationCodeCreate;
import com.lagou.edu.entity.LagouAuthCode;
import com.lagou.edu.util.UtilForData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RequestMapping("verificationCode")
@RestController
@RefreshScope
public class VerificationCodeCreateControl implements VerificationCodeCreate {

    private final SendEmail sendEmail;

    private final LagouAuthCodeDao lagouAuthCodeDao;

    public VerificationCodeCreateControl(@Qualifier("com.lagou.edu.SendEmail")SendEmail sendEmail, LagouAuthCodeDao lagouAuthCodeDao) {
        this.sendEmail = sendEmail;
        this.lagouAuthCodeDao = lagouAuthCodeDao;
    }

    @Value("${code-setting.expire-time}")
    private Long expireTime;
    /**
     * 生产验证码并通过邮箱发送
     *
     * @return 验证码
     */
    @Override
    @GetMapping("/createCodeAndSendEmail/{email}")
    public ResModel<?> createCodeAndSendEmail( @PathVariable String email ) {

        //  生成6位数随机码
        String vCode = UtilForData.randomCodeByDigit();

        try {
            //  储存验证码
            LagouAuthCode lagouAuthCode = new LagouAuthCode();
            lagouAuthCode.setEmail(email);
            lagouAuthCode.setCode(vCode);
            LocalDateTime now = LocalDateTime.now();
            lagouAuthCode.setCreateTime( now );
            LocalDateTime expireDateTime = now.plusMinutes(expireTime);
            lagouAuthCode.setExpireTime(expireDateTime);
            lagouAuthCodeDao.save(lagouAuthCode);

            //  发送邮件
            return sendEmail.sendVerificationCode(vCode,email)
                    ? ResModel.SUCCESS()
                    :ResModel.FAIL("验证码发送失败");
        }catch (Exception e){
            e.printStackTrace();
            return ResModel.FAIL("验证码发送失败");
        }
    }
}
