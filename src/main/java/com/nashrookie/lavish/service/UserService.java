package com.nashrookie.lavish.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.constant.Role;
import com.nashrookie.lavish.dto.request.LoginRequest;
import com.nashrookie.lavish.dto.request.RegisterRequest;
import com.nashrookie.lavish.dto.response.AuthResponse;
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.exception.UsernameAlreadyExistsException;
import com.nashrookie.lavish.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws UsernameAlreadyExistsException {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        String encryptedPassword = encoder.encode(request.password());

        User newUser = userRepository.save(User.builder().username(request.username()).password(encryptedPassword)
                .firstname(request.firstname()).lastname(request.lastname())
                .isActive(true).role(Role.USER)
                .build());

        String jwt = jwtService.generateAccessToken(newUser);

        return AuthResponse.builder().id(newUser.getId()).username(newUser.getUsername()).accessToken(jwt).build();
    }

    public AuthResponse verify(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = (User) authentication.getPrincipal();
        String jwt = jwtService.generateAccessToken(user);

        return AuthResponse.builder().id(user.getId()).username(user.getUsername()).accessToken(jwt).build();
    }
}
