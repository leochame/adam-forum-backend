package com.adam.auth.service.impl;

import com.adam.auth.mapper.UserMapper;
import com.adam.auth.model.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.auth.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author chenjiahan
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-14 12:04:36
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}




