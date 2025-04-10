package com.nashrookie.lavish.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nashrookie.lavish.entity.User;

@Component
public class JwtService {

    private final Algorithm algorithm;

    public JwtService (@Value("${application.jwt.secretkey}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
    }

    public String generateAccessToken (User user) {
        try {
            return JWT.create()
                      .withSubject(user.getUsername())
                      .withClaim("username", user.getUsername())
                      .withClaim("role", user.getRole().toString())
                      .withExpiresAt(genAccessExpirationDate())
                      .sign(algorithm);
        }
        catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateToken (String token) {
        try {
            return JWT.require(algorithm)
                      .build()
                      .verify(token)
                      .getSubject();
        }
        catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    private Instant genAccessExpirationDate () {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC);
    }

}
