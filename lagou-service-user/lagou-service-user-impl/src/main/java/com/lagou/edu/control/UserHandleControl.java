package com.lagou.edu.control;

import com.lagou.edu.ResModel;
import com.lagou.edu.dao.LagouAuthCodeDao;
import com.lagou.edu.dao.LagouTokenDao;
import com.lagou.edu.dao.LagouUserDao;
import com.lagou.edu.entity.LagouAuthCode;
import com.lagou.edu.entity.LagouToken;
import com.lagou.edu.entity.LagouUser;
import edu.UserHandle;
import edu.request.UserRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("user")
@RestController
@AllArgsConstructor
public class UserHandleControl implements UserHandle {

    private final LagouUserDao lagouUserDao;
    private final LagouAuthCodeDao lagouAuthCodeDao;
    private final LagouTokenDao lagouTokenDao;
    private final HttpServletResponse response;
    /**
     * 登入提交接口
     * @param email     登入邮箱
     * @param password  登入密码
     * @return          返回登入状态的实体
     */
    @Override
    @GetMapping("login/{email}/{password}")
    public ResModel<?> login( @PathVariable("email") String email, @PathVariable("password") String password ) {

        String  tokenId;
        Optional<LagouUser> user;
        try {
        // 检查账号密码
        LagouUser lagouUser = new LagouUser();
        lagouUser.setUsername(email);
        user = lagouUserDao.findOne(Example.of(lagouUser));

        if ( user.isEmpty() || !user.get().getPassword().equals(password) ) {
            return ResModel.FAIL("用户或者密码不正确！");
        }
        //  生成tokenId并保存到数据库
        tokenId = createTokenId(email);

        }catch (Exception e){
            e.printStackTrace();
            return ResModel.FAIL("系统异常！");
        }

        Cookie cookie = new Cookie("tokenId", tokenId);
        System.out.println(tokenId);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);

        return ResModel.SUCCESS("登入成功！",cookie);
    }

    /**
     * 注册提交接口(为了简化这里就不给密码加密)
     *
     * @param userRequest 用户注册参数
     */
    @Override
    @PostMapping("register")
    public ResModel<?> register(@RequestBody @Validated UserRequest userRequest) {

        try {

            //  判断用户是否已经存在
            LagouUser user = new LagouUser();
            user.setUsername(userRequest.getEmail());
            Optional<LagouUser> one = lagouUserDao.findOne(Example.of(user));
            if ( one.isPresent() ) {
                return ResModel.FAIL("用户已存在");
            }
            //  验证码验证
            LagouAuthCode lagouAuthCode = lagouAuthCodeDao.findFirstByEmailOrderByExpireTimeDesc(userRequest.getEmail());
            LocalDateTime expireTime = lagouAuthCode == null ? null : lagouAuthCode.getExpireTime();
            if ( expireTime == null || LocalDateTime.now().isAfter(expireTime) ) {
                return ResModel.FAIL("验证码失效,请重新获取验证码");
            }
            if ( !userRequest.getCode().equals(lagouAuthCode.getCode()) ) {
                return ResModel.FAIL("验证码不正确！");
            }

            //  将用户信息保存入库
            LagouUser lagouUser = new LagouUser();
            lagouUser.setUsername(userRequest.getEmail());
            lagouUser.setPassword(userRequest.getPassword());
            lagouUser.setCreateTime(LocalDateTime.now());
            lagouUserDao.save(lagouUser);

        }catch (Exception e){
            e.printStackTrace();
            return ResModel.FAIL("注册失败");
        }
        return ResModel.SUCCESS();
    }

    /**
     * 生成tokenId并保存到数据库
     */
    private String createTokenId ( String email ){
        //  生成token信息并记录到数据库中
        String uuid = UUID.randomUUID().toString();
        LagouToken lagouToken = new LagouToken();
        lagouToken.setToken(uuid);
        lagouToken.setEmail(email);
        LocalDateTime now = LocalDateTime.now();
        lagouToken.setCreateTime(now);
        LocalDateTime expireTime = now.plusMinutes(30); //  30分钟后过期
        lagouToken.setExpireTime(expireTime);
        lagouTokenDao.save(lagouToken);
        return uuid;
    }

    @GetMapping("info/{tokenId}")
    private ResModel<LagouUser> getUserInfo( @PathVariable("tokenId") String tokenId ){

        //  根据token获取username
        LagouToken lagouToken = new LagouToken();
        lagouToken.setToken( tokenId );
        Optional<LagouToken> tokenDaoOne = lagouTokenDao.findOne(Example.of(lagouToken));
        if ( tokenDaoOne.isEmpty() || LocalDateTime.now().isAfter(tokenDaoOne.get().getExpireTime()) ) {
            return ResModel.FAIL("token无效或者过期");
        }

        //  根据username获取user信息
        LagouUser lagouUser = new LagouUser();
        lagouUser.setUsername(tokenDaoOne.get().getEmail());
        Optional<LagouUser> user = lagouUserDao.findOne(Example.of(lagouUser));
        if ( user.isEmpty() ) {
            return ResModel.FAIL("查询不到用户信息");
        }

        return ResModel.SUCCESS(user.get());
    }
}
