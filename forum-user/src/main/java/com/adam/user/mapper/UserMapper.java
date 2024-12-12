package com.adam.user.mapper;

import com.adam.user.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author iceman
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-12-12 21:19:05
* @Entity com.adam.user.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

}




