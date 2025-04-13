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

    private final Algorithm accessAlgorithm;
    private final Algorithm refreshAlgorithm;
    private static final Integer ACCESS_TOKEN_EXPIRATION = 60;
    private static final Integer REFRESH_TOKEN_EXPIRATION = 1440;

    public JwtService (@Value("${application.jwt.secretkey}") String jwtSecret, @Value("${application.jwt.refresh-secretkey}") String refreshSecret) {
        this.accessAlgorithm = Algorithm.HMAC256(jwtSecret);
        this.refreshAlgorithm = Algorithm.HMAC256(refreshSecret);
    }

    public String generateAccessToken (User user) {
        try {
            return JWT.create()
                      .withSubject(user.getUsername())
                      .withClaim("username", user.getUsername())
                      .withClaim("role", user.getRole().toString())
                      .withExpiresAt(genAccessExpirationDate(ACCESS_TOKEN_EXPIRATION))
                      .sign(accessAlgorithm);
        }
        catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateAccessToken (String token) {
        try {
            return JWT.require(accessAlgorithm)
                      .build()
                      .verify(token)
                      .getSubject();
        }
        catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    public String generateRefreshToken (User user) {
        try {
            return JWT.create()
                      .withSubject(user.getUsername())
                      .withClaim("username", user.getUsername())
                      .withClaim("role", user.getRole().toString())
                      .withExpiresAt(genAccessExpirationDate(REFRESH_TOKEN_EXPIRATION))
                      .sign(refreshAlgorithm);
        }
        catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateRefreshToken (String token) {
        try {
            return JWT.require(accessAlgorithm)
                      .build()
                      .verify(token)
                      .getSubject();
        }
        catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    private Instant genAccessExpirationDate (Integer minutes) {
        return LocalDateTime.now().plusMinutes(minutes).toInstant(ZoneOffset.UTC);
    }

}
