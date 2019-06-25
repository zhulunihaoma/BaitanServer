package com.xll.baitaner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@MapperScan("com.xll.baitaner.mapper")
@PropertySource(value={"application.properties","WEB-INF/classes/application.properties"},ignoreResourceNotFound = true)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
