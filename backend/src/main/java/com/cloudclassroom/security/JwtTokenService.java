package com.cloudclassroom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 生成与解析服务。
 */
@Service
public class JwtTokenService {
  private final JwtProperties jwtProperties;

  public JwtTokenService(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  private Key getKey() {
    // 注意：HS256 需要足够长度的 secret
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Long userId, String roleCode) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + jwtProperties.getExpireSeconds() * 1000);

    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("role", roleCode)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
