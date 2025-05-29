package com.asamurik_rest_api.utils;

import com.asamurik_rest_api.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private String ok;
    private final SecretKey secretKey;
    private final Long timeExpiration;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(JwtConfig jwtConfig) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecretKey()));
        this.timeExpiration = jwtConfig.getTimeExpiration();
    }

    public Map<String, Object> mappingBodyToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", claims.get("id"));
        map.put("fullname", claims.get("fullname"));
        map.put("username", claims.getSubject());
        map.put("email", claims.get("email"));
        map.put("phoneNumber", claims.get("phoneNumber"));

        return map;
    }

    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser().setSigningKey(JwtConfig.getSecretKey()).parseClaimsJws(token).getBody();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        long timeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(timeMillis))
                .setExpiration(new Date(timeMillis + timeExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token) {
        // Sudah otomatis tervalidasi jika expired date masih aktif
        String username = getUsernameFromToken(token);
        return (username != null && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).get("username", String.class);
    }

    public String getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", String.class);
    }
}
