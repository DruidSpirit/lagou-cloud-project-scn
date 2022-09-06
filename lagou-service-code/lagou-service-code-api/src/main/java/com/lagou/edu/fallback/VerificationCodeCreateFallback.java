package com.lagou.edu.fallback;

import com.lagou.edu.ResModel;
import com.lagou.edu.api.VerificationCodeCreate;

public class VerificationCodeCreateFallback implements VerificationCodeCreate {

    @Override
    public ResModel createCodeAndSendEmail( String email) {
        return ResModel.FAIL("服务器熔断");
    }
}
