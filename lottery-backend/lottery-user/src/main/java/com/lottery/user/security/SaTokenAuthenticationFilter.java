package com.lottery.user.security;

import cn.dev33.satoken.stp.StpUtil;
import com.lottery.user.service.UserAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SaTokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserAccountService userAccountService;

    public SaTokenAuthenticationFilter(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 仅在 SecurityContext 为空时填充——如果 Keycloak 已认证则不覆盖，实现双认证退避
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                // 将 SaToken 会话 ID 转换为 Spring Security Authentication，密码传 null（不在此处暴露）
                userAccountService.findAuthenticatedUser(Long.parseLong(String.valueOf(loginId)))
                        .ifPresent(user -> {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            }
        }
        filterChain.doFilter(request, response);
    }
}
