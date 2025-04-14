package com.nashrookie.lavish.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nashrookie.lavish.constant.Role;
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

    public String generateAccessToken(String username, Role role) {
        try {
            return JWT.create()
                    .withSubject(username)
                    .withClaim("username", username)
                    .withClaim("role", role.toString())
                    // .withExpiresAt(genExpirationDate(ACCESS_TOKEN_EXPIRATION))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 15)) // TODO: Only for test. Last 15 seconds
                    .sign(accessAlgorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateAccessToken(String token) {
        try {
            return JWT.require(accessAlgorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    public String generateRefreshToken(String username, Role role) {
        try {
            return JWT.create()
                    .withSubject(username)
                    .withClaim("username", username)
                    .withClaim("role", role.toString())
                    //.withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 2)) // TODO: Only for test. Last 2 minutes
                    .sign(refreshAlgorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
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

    public Role getRole(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("role").as(Role.class);
    }
}
