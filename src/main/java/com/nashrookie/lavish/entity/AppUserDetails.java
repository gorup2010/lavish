package com.nashrookie.lavish.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class AppUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Set<Role> roles;

    public AppUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roles = user.getRoles();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r.toString())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
