package com.ticktracker.gatewayservice.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    public JwtUtil(@Value("${jwt.secret-key}") String key)
    {
        this.secretKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }


    public Claims parseToken(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }


    public String parseSubject(String token)
    {
        return parseToken(token).getSubject();
    }

    public Long parseUserId(String token)
    {
        return parseToken(token).get("userId",Long.class);
    }

    public String parseRole(String token)
    {
        return parseToken(token).get("role",String.class);
    }
    public boolean validateToken(String token)
    {
        try
        {
            parseToken(token);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
