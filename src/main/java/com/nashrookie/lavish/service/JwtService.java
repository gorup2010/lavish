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

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtService {
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String ROLES = "roles";

    private final Integer ACCESS_TOKEN_EXPIRATION;
    private final Integer REFRESH_TOKEN_EXPIRATION;
    private final Algorithm accessAlgorithm;
    private final Algorithm refreshAlgorithm;

    public JwtService(@Value("${application.jwt.access-secretkey}") String accessSecret,
            @Value("${application.jwt.refresh-secretkey}") String refreshSecret,
            @Value("${application.jwt.access-lifetime}") Integer accessLifetime,
            @Value("${application.jwt.refresh-lifetime}") Integer refreshLifetime) {
        this.accessAlgorithm = Algorithm.HMAC256(accessSecret);
        this.refreshAlgorithm = Algorithm.HMAC256(refreshSecret);
        this.ACCESS_TOKEN_EXPIRATION = accessLifetime;
        this.REFRESH_TOKEN_EXPIRATION = refreshLifetime;
    }

    public String generateAccessToken(Long id, String username, List<String> roles) {
        try {
            return JWT.create()
                    .withSubject(username)
                    .withClaim(ID, id)
                    .withClaim(ROLES, roles)
                    .withClaim(USERNAME, username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                    .sign(accessAlgorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating access token", exception);
        }
    }

    public String generateAccessToken(Long id, String username, Set<Role> roles) {
        return this.generateAccessToken(id, username, roles.stream().map(Role::getName).toList());
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

    public String generateRefreshToken(Long id, String username, List<String> roles) {
        try {
            return JWT.create()
                    .withSubject(username)
                    .withClaim(ID, id)
                    .withClaim(ROLES, roles)
                    .withClaim(USERNAME, username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
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
            log.error("Error while validating refresh token {}", token);
            throw new RefreshTokenInvalidException();
        }
    }

    public List<String> getStringRoleList(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim(ROLES).asList(String.class);
    }

    public Long getId(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim(ID).asLong();
    }

    public Long getRemaingTimeToLiveInSeconds(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return (decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000;
    }
}
