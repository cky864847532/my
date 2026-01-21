package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import com.taizhou.kailv.api.model.User;
import com.taizhou.kailv.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");

        User u = userService.findByUsername(username);
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }
        if (!passwordEncoder.matches(password, u.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials","real",u.getPassword(),"yours",password,"yoursencoding",passwordEncoder.encode(password)));
        }

        String token = com.taizhou.kailv.api.security.JwtUtil.generateToken(username);
        String refresh = com.taizhou.kailv.api.security.JwtUtil.generateRefreshToken(username);
        return ResponseEntity.ok(Map.of("token", token, "refreshToken", refresh, "expires_in", 3600, "refresh_expires_in", 604800));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.getOrDefault("refreshToken", "");
        if (!com.taizhou.kailv.api.security.JwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_refresh_token"));
        }
        String username = com.taizhou.kailv.api.security.JwtUtil.getUsername(refreshToken);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_refresh_token"));
        }
        String newToken = com.taizhou.kailv.api.security.JwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token", newToken, "expires_in", 3600));
    }
}
