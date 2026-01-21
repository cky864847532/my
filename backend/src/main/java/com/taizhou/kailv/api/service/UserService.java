package com.taizhou.kailv.api.service;

import com.taizhou.kailv.api.model.User;

public interface UserService {
    User findByUsername(String username);
    void createIfNotExists(String username, String rawPassword, String roles);
}
