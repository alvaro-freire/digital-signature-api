package com.example.digitalsignatureapi.config;

import com.example.digitalsignatureapi.exception.InvalidApiPasswordException;
import com.example.digitalsignatureapi.exception.InvalidTokenException;
import com.example.digitalsignatureapi.exception.MissingHeaderException;
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

        try {
            if ("/api/authenticate".equals(path)) {
                if (token == null || !token.equals("Bearer " + apiPassword)) {
                    logger.warn("Invalid API password");
                    throw new InvalidApiPasswordException();
                }
                Authentication authentication = new PreAuthenticatedAuthenticationToken("authenticatedUser", null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtService.validateToken(jwtToken)) {
                    String userId = jwtService.getUserIdFromToken(jwtToken);
                    logger.info("Authenticated userId from token: {}", userId);
                    Authentication authentication = new PreAuthenticatedAuthenticationToken(userId, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("Invalid or expired token");
                    throw new InvalidTokenException();
                }
            } else {
                throw new MissingHeaderException("Authorization");
            }
        } catch (InvalidApiPasswordException | InvalidTokenException | MissingHeaderException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
