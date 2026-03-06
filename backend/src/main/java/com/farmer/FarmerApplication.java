package com.farmer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.farmer.modules.**.mapper")
public class FarmerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmerApplication.class, args);
    }
}
