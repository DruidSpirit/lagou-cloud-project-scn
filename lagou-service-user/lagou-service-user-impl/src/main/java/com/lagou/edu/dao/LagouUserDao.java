package com.lagou.edu.dao;

import com.lagou.edu.entity.LagouUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LagouUserDao   extends JpaRepository<LagouUser,Integer> {
}
