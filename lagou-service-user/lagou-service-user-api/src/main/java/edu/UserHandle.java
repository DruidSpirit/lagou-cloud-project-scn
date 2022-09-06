package edu;

import com.lagou.edu.ResModel;
import edu.request.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("lagou-service-user")
@RequestMapping("user")
public interface UserHandle {

    /**
     * 登入提交接口
     * @param email     登入邮箱
     * @param password  登入密码
     * @return          返回登入状态的实体
     */
    @GetMapping("login")
    ResModel<?> login(@PathVariable("email") String email, @PathVariable("password") String password  );

    /**
     * 注册提交接口
     * @param userRequest 用户注册参数
     */
    @PostMapping("register")
    ResModel<?> register ( UserRequest userRequest );

}
