package com.nashrookie.lavish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.dto.filter.UserFilterDto;
import com.nashrookie.lavish.dto.request.LoginRequest;
import com.nashrookie.lavish.dto.request.RegisterRequest;
import com.nashrookie.lavish.dto.request.UpdateUserIsActiveDto;
import com.nashrookie.lavish.dto.response.AuthResponse;
import com.nashrookie.lavish.dto.response.UserInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.BlockedUser;
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.entity.Role;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.exception.UsernameAlreadyExistsException;
import com.nashrookie.lavish.repository.BlockedUserRepository;
import com.nashrookie.lavish.repository.RoleRepository;
import com.nashrookie.lavish.repository.UserRepository;
import com.nashrookie.lavish.specification.UserSpecification;
import com.nashrookie.lavish.util.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    @Value("${application.jwt.refresh-lifetime}")
    private Long refreshLifetime;

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final BlockedUserRepository blockedUserRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws UsernameAlreadyExistsException {
        if (userRepository.findByIsActiveTrueAndUsername(request.email()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        String encryptedPassword = encoder.encode(request.password());

        Role userRole = roleRepository.findByName("USER").orElse(null);
        User newUser = userRepository.save(User.builder().username(request.email()).password(encryptedPassword)
                .firstname(request.firstname()).lastname(request.lastname())
                .build());
        newUser.addRole(userRole);

        String jwt = jwtService.generateAccessToken(newUser.getId(), newUser.getUsername(), newUser.getRoles());
        List<String> stringRoles = newUser.getRoles().stream().map(Role::getName).toList();

        return AuthResponse.builder().id(newUser.getId()).username(newUser.getUsername()).roles(stringRoles)
                .accessToken(jwt).build();
    }

    public AuthResponse verify(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());
        List<String> stringRoles = user.getRoles().stream().map(Role::getName).toList();

        return AuthResponse.builder().id(user.getId()).username(user.getUsername()).accessToken(jwt)
                .roles(stringRoles).build();
    }

    public PaginationResponse<UserInAdminDto> getUsersAdminView(UserFilterDto userFilter,
            Pageable pageable) {
        Page<User> products = this.getUsers(userFilter, pageable);

        return PaginationUtils.createPaginationResponse(products, UserInAdminDto::fromModel);
    }

    public Page<User> getUsers(UserFilterDto userFilter, Pageable pageable) {
        return userRepository.findAll(Specification.where(UserSpecification.hasName(userFilter.username())),
                pageable);
    }

    @Transactional
    public void updateUserActiveStatus(Long id, UpdateUserIsActiveDto isActiveDto) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Not found user with id {} in updateUserActiveStatus", id);
            return new ResourceNotFoundException();
        });

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new AuthorizationDeniedException("Cannot change admin status");
        }

        if (isActiveDto.isActive()) {
            blockedUserRepository.deleteById(user.getUsername());
        }
        else {
            blockedUserRepository.save(new BlockedUser(user.getUsername(), refreshLifetime / 1000));
        }

        user.setIsActive(isActiveDto.isActive());
        userRepository.save(user);
    }
}
