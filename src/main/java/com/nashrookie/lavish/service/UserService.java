package com.nashrookie.lavish.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.dto.request.LoginRequest;
import com.nashrookie.lavish.dto.request.RegisterRequest;
import com.nashrookie.lavish.dto.response.AuthResponse;
import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.Role;
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.exception.UsernameAlreadyExistsException;
import com.nashrookie.lavish.repository.RoleRepository;
import com.nashrookie.lavish.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws UsernameAlreadyExistsException {
        if (userRepository.findByUsername(request.email()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        String encryptedPassword = encoder.encode(request.password());

        Role userRole = roleRepository.findByName("USER").orElse(null);
        User newUser = userRepository.save(User.builder().username(request.email()).password(encryptedPassword)
                .firstname(request.firstname()).lastname(request.lastname())
                .build());
        newUser.addRole(userRole);

        String jwt = jwtService.generateAccessToken(newUser.getUsername(), newUser.getRoles());
        List<String> stringRoles = newUser.getRoles().stream().map(Role::getName).toList();

        return AuthResponse.builder().id(newUser.getId()).username(newUser.getUsername()).roles(stringRoles).accessToken(jwt).build();
    }

    public AuthResponse verify(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        List<String> stringRoles = user.getRole().stream().map(Role::getName).toList();

        return AuthResponse.builder().id(user.getId()).username(user.getUsername()).accessToken(jwt)
                .roles(stringRoles).build();
    }
}
