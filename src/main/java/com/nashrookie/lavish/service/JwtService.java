package com.nashrookie.lavish.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.nashrookie.lavish.entity.Role;
import com.nashrookie.lavish.exception.RefreshTokenInvalidException;

@Component
public class JwtService {

    private final Algorithm accessAlgorithm;
    private final Algorithm refreshAlgorithm;
    private static final Integer ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 minutes in millis
    private static final Integer REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // One day in millis

    public JwtService(@Value("${application.jwt.secretkey}") String accessSecret,
            @Value("${application.jwt.refresh-secretkey}") String refreshSecret) {
        this.accessAlgorithm = Algorithm.HMAC256(accessSecret);
        this.refreshAlgorithm = Algorithm.HMAC256(refreshSecret);
    }

    public String generateAccessToken(String username, List<String> roles) {
        try {
            return JWT.create()
                    .withSubject(username)
                    .withClaim("username", username)
                    .withClaim("roles", roles)
                    .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                    //.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 15)) Only for test. Last 15 seconds
                    .sign(accessAlgorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating access token", exception);
        }
    }

    public String generateAccessToken(String username, Set<Role> roles) {
        return this.generateAccessToken(username, roles.stream().map(Role::getName).toList());
    }

    public String validateAccessToken(String token) {
        try {
            return JWT.require(accessAlgorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating access token", exception);
        }
    }

    public String generateRefreshToken(String username, List<String> roles) {
        try {
            return JWT.create()
                    .withSubject(username)
                    .withClaim("username", username)
                    .withClaim("roles", roles)
                    .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                    //.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 2)) Only for test. Last 2 minutes
                    .sign(refreshAlgorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating refresh token", exception);
        }
    }

    public String validateRefreshToken(String token) {
        try {
            return JWT.require(refreshAlgorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RefreshTokenInvalidException();
        }
    }

    public List<String> getStringRoleList(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }
}
