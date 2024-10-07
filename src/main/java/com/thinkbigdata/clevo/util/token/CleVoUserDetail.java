package com.thinkbigdata.clevo.util.token;

import com.thinkbigdata.clevo.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


public class CleVoUserDetail implements UserDetails {
    private String email;
    private Collection<GrantedAuthority> role;

    public CleVoUserDetail(String email, Role role) {
        this.email = email;
        this.role = new ArrayList<>();
        if (role.toString().equals("ADMIN")) this.role.add(new SimpleGrantedAuthority(Role.ADMIN.toString()));
        this.role.add(new SimpleGrantedAuthority(Role.USER.toString()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
