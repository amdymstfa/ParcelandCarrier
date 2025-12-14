package com.logistics.parcelandcarrier.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter
 * Intercepts all requests and validates JWT tokens
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CustomUserDetailsService userDetailsService;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final List<String> PUBLIC_ENDPOINTS = List.of(
    "/api/auth/login",
    "/swagger-ui",
    "/v3/api-docs",
    "/actuator"
  );

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    String requestPath = request.getRequestURI();
    log.debug("Processing request: {} {}", request.getMethod(), requestPath);

    // Skip filter for public endpoints
    if (isPublicEndpoint(requestPath)) {
      log.debug("Public endpoint, skipping JWT validation");
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Extract JWT token from header
      String token = extractTokenFromRequest(request);

      if (token == null) {
        log.warn("No JWT token found in request");
        filterChain.doFilter(request, response);
        return;
      }

      // Validate token
      if (!jwtService.validateToken(token)) {
        log.warn("Invalid JWT token");
        filterChain.doFilter(request, response);
        return;
      }

      // Extract username and authenticate
      String username = jwtService.extractUsername(token);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        authenticateUser(token, username, request);
      }

    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract JWT token from Authorization header
   */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }

    return null;
  }

  /**
   * Authenticate user with JWT token
   */
  private void authenticateUser(String token, String username, HttpServletRequest request) {
    try {
      // Load user details
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // Create authentication token
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
      );

      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Set authentication in SecurityContext
      SecurityContextHolder.getContext().setAuthentication(authToken);

      log.debug("User authenticated: {} with roles: {}", username, userDetails.getAuthorities());

    } catch (Exception e) {
      log.error("Failed to authenticate user: {}", e.getMessage());
    }
  }

  /**
   * Check if endpoint is public (no authentication required)
   */
  private boolean isPublicEndpoint(String path) {
    return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
  }
}
