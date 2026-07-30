package com.zzx.jiachangcai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.zzx.jiachangcai.module.*.mapper", "com.zzx.jiachangcai.module.social.follow.module.mapper", "com.zzx.jiachangcai.module.social.favorite.mapper"})
public class JiachangcaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiachangcaiApplication.class, args);
    }

}
