package com.taizhou.kailv.api.init;

import com.taizhou.kailv.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class UserDataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) {
        // create default users if missing
        userService.createIfNotExists("user", "testpass", "ROLE_USER");
        userService.createIfNotExists("admin", "password", "ROLE_ADMIN");
    }
}
