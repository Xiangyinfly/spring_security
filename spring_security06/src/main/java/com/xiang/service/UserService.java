package com.xiang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiang.entity.User;
import com.xiang.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends IService<User>, UserDetailsService {
}
