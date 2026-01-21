package com.taizhou.kailv.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // NOTE: For production, move secret to config and keep it safe.
    private static final String SECRET = "kailv-dev-secret-please-change-to-secure-key-2025";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXP_MS = 3600_000L; // 1 hour
    private static final long EXP_MS_REFRESH = 7L * 24 * 3600_000L; // 7 days

    public static String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXP_MS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("type", "access")
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXP_MS_REFRESH);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("type", "refresh")
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        return getUsername(token) != null;
    }

    public static boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
            String t = claims.get("type", String.class);
            return "refresh".equals(t);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRefreshToken(String token) {
        return validateRefreshToken(token);
    }
}
