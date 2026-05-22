package com.lottery.user.security;

import com.lottery.user.domain.entity.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthenticatedUser implements UserDetails {

    private final UserAccount userAccount;

    public AuthenticatedUser(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Long getUserId() {
        return userAccount.getId();
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    // 硬编码 ROLE_USER：Spring Security 要求认证主体至少持有一个 GrantedAuthority，
    // 此处的单一角色仅用于满足框架约定，实际 RBAC 权限由数据库角色-菜单关联决定
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(userAccount.getEnabled());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(userAccount.getEnabled());
    }
}
