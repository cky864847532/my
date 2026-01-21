package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taizhou.kailv.api.mapper.UserMapper;
import com.taizhou.kailv.api.model.User;
import com.taizhou.kailv.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public void createIfNotExists(String username, String rawPassword, String roles) {
        User u = findByUsername(username);
        if (u != null) return;
        User n = new User();
        n.setUsername(username);
        n.setPassword(passwordEncoder.encode(rawPassword));
        n.setRoles(roles);
        n.setCreatedAt(LocalDateTime.now());
        userMapper.insert(n);
    }
}
