package com.lagou.edu.dao;

import com.lagou.edu.entity.LagouAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LagouAuthCodeDao extends JpaRepository<LagouAuthCode,Integer> {
}
