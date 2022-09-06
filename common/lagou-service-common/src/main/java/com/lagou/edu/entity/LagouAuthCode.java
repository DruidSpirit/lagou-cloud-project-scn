package com.lagou.edu.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="lagou_auth_code")
public class LagouAuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String code;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;

}
