package com.lagou.edu.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="lagou_token")
public class LagouToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String token;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
}
