package com.example.asamurik_rest_api.security;

import com.example.asamurik_rest_api.common.CustomRequestWrapper;
import com.example.asamurik_rest_api.service.AuthService;
import com.example.asamurik_rest_api.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");;
        authorization = authorization == null ? "" : authorization;
        String token = "";
        String username = "";

        try {
            if (!"".equals(authorization) &&
                    authorization.startsWith("Bearer ") &&
                    authorization.length() > 7
            ) {
                token = authorization.substring(7);
                username = jwtUtil.getUsernameFromToken(token);

                String contentType = request.getContentType() == null ? "" : request.getContentType();
                if (!contentType.startsWith("multipart/form-data") || "".equals(contentType)) {
                    request = new CustomRequestWrapper(request);
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.validateToken(token)) {
                        UserDetails userDetails = authService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("JWT Filter Exception: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
