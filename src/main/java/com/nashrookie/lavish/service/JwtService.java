package com.nashrookie.lavish.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nashrookie.lavish.entity.Role;
import com.nashrookie.lavish.entity.Token;
import com.nashrookie.lavish.exception.RefreshTokenInvalidException;
import com.nashrookie.lavish.repository.BlockedUserRepository;
import com.nashrookie.lavish.repository.TokenRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtService {
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String ROLES = "roles";

    private BlockedUserRepository blockedUserRepository;
    private TokenRepository tokenRepository;
    private final Integer ACCESS_TOKEN_EXPIRATION;
    private final Integer REFRESH_TOKEN_EXPIRATION;
    private final Algorithm accessAlgorithm;
    private final Algorithm refreshAlgorithm;

    @Autowired
    public void setBlockedUserRepository(BlockedUserRepository blockedUserRepository) {
        this.blockedUserRepository = blockedUserRepository;
    }

    @Autowired
    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public JwtService(@Value("${application.jwt.access-secretkey}") String accessSecret,
            @Value("${application.jwt.refresh-secretkey}") String refreshSecret,
            @Value("${application.jwt.access-lifetime}") Integer accessLifetime,
            @Value("${application.jwt.refresh-lifetime}") Integer refreshLifetime) {
        this.accessAlgorithm = Algorithm.HMAC256(accessSecret);
        this.refreshAlgorithm = Algorithm.HMAC256(refreshSecret);
        this.ACCESS_TOKEN_EXPIRATION = accessLifetime;
        this.REFRESH_TOKEN_EXPIRATION = refreshLifetime;
    }

    public void revokeToken(String token) {
        tokenRepository.save(new Token(token, this.getRemaingTimeToLiveInSeconds(token)));
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
            String username = JWT.require(accessAlgorithm)
                    .build()
                    .verify(token)
                    .getSubject();

            blockedUserRepository.findById(username).ifPresent((user) -> {
                log.error("In validateAccessToken, user {} is blocked", username);
                throw new JWTVerificationException("");
            });

            return username;
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
            String username = JWT.require(refreshAlgorithm)
                    .build()
                    .verify(token)
                    .getSubject();

            tokenRepository.findById(token).ifPresent((t) -> {
                log.error("In validateRefreshToken, Refresh token {} already revoked", token);
                throw new JWTVerificationException("");
            });
            blockedUserRepository.findById(username).ifPresent((user) -> {
                log.error("In validateRefreshToken, user {} is blocked", username);
                throw new JWTVerificationException("");
            });

            return username;
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
