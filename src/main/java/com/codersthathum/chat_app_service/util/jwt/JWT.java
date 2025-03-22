package com.codersthathum.chat_app_service.util.jwt;

import com.codersthathum.chat_app_service.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWT {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private Key key;

    public static final String ACCESS_TOKEN_TYPE = "access_token";

    public static final String REFRESH_TOKEN_TYPE = "refresh_token";

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId) {
        return generateToken(userId, ACCESS_TOKEN_TYPE);
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(userId, REFRESH_TOKEN_TYPE);
    }

    private String generateToken(Long userId, String tokenType) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (tokenType.equals(ACCESS_TOKEN_TYPE) ? accessTokenExpiration : refreshTokenExpiration)))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String type) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("type", String.class).equals(type);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired");
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public Long getUserIdFromToken(String token) {
        String id = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(id);
    }
}
