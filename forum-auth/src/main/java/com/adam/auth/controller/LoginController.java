package com.adam.auth.controller;

import com.adam.auth.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 12:10
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "登录相关接口")
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;


}
