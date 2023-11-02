package com.xiang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiang.entity.Perm;
import com.xiang.entity.User;
import com.xiang.mapper.PermMapper;
import com.xiang.mapper.UserMapper;
import com.xiang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermMapper permMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username",username);
        User user = userMapper.selectOne(userQueryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户未找到");
        }

        //根据用户名查找权限
        QueryWrapper<Perm> permQueryWrapper = new QueryWrapper<>();
        permQueryWrapper.eq("user_id",user.getId());
        List<Perm> perms = permMapper.selectList(permQueryWrapper);
        List<String> permTags = perms.stream().map(Perm::getTag).toList();//权限标识
        user.setAuthorities(AuthorityUtils.createAuthorityList(permTags));
        return user;
    }
}
