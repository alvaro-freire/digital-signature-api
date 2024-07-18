package com.example.digitalsignatureapi.config;

import com.example.digitalsignatureapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthorizationFilter.class);

    private final String apiPassword;

    @Autowired
    private JwtService jwtService;

    public CustomAuthorizationFilter(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String token = request.getHeader("Authorization");

        if ("/api/authenticate".equals(path)) {
            if (token == null || !token.equals("Bearer " + apiPassword)) {
                logger.warn("Invalid API password");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API password");
                return;
            }
            Authentication authentication = new PreAuthenticatedAuthenticationToken("authenticatedUser", null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwtToken = token.substring(7);
                if (jwtService.validateToken(jwtToken)) {
                    String userId = jwtService.getUserIdFromToken(jwtToken);
                    logger.info("Authenticated userId from token: {}", userId);
                    Authentication authentication = new PreAuthenticatedAuthenticationToken(userId, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("Invalid or expired token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                logger.error("Invalid token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required header: Authorization");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
