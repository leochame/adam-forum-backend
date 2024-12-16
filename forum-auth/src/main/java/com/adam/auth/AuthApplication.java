package com.adam.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 11:58
 */
@SpringBootApplication(scanBasePackages = {"com.adam"})
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
