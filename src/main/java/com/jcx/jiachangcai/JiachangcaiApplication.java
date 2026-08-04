package com.jcx.jiachangcai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.jcx.jiachangcai.module.**.mapper", "com.jcx.jiachangcai.module.order.cart.mapper"})
public class JiachangcaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiachangcaiApplication.class, args);
    }

}
