package com.nashrookie.lavish.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findWithRolesByUsername(username).orElseThrow(() -> {
            log.info("Not found user {}", username);
            return new UsernameNotFoundException("User not found");
        });
        return AppUserDetails.builder().id(user.getId()).username(user.getUsername()).password(user.getPassword())
                .roles(user.getRoles()).build();
    }
}