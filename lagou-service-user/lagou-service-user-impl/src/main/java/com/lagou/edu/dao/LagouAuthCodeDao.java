package com.lagou.edu.dao;

import com.lagou.edu.entity.LagouAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LagouAuthCodeDao extends JpaRepository<LagouAuthCode,Integer> {

    /**
     * 根据邮箱查找最新一条验证码的记录
     * @param email     查询邮箱
     * @return          查询到的结果验证码
     */
    LagouAuthCode findFirstByEmailOrderByExpireTimeDesc(String email);
}
