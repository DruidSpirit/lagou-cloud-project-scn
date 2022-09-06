package com.lagou.edu.dao;

import com.lagou.edu.entity.LagouToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LagouTokenDao extends JpaRepository<LagouToken,Integer> {
}
