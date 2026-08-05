package com.jcx.jiachangcai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.jcx.jiachangcai.module.**.mapper", sqlSessionTemplateRef = "mysqlSqlSessionTemplate")
public class JiachangcaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiachangcaiApplication.class, args);
    }

}
