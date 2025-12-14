package com.logistics.parcelandcarrier.service;

import com.logistics.parcelandcarrier.dto.request.LoginRequest;
import com.logistics.parcelandcarrier.dto.response.LoginResponse;
import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.exception.UnauthorizedException;
import com.logistics.parcelandcarrier.repository.UserRepository;
import com.logistics.parcelandcarrier.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for authentication operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  /**
   * Authenticate user and generate JWT token
   * @param request login credentials
   * @return LoginResponse with JWT token
   */
  public LoginResponse authenticate(LoginRequest request) {
    log.info("Authentication attempt for user: {}", request.getLogin());

    // Find user by login
    User user = userRepository.findByLogin(request.getLogin())
      .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

    // Check if user is active
    if (!user.isActive()) {
      log.warn("Inactive user attempted to login: {}", request.getLogin());
      throw new UnauthorizedException("User account is deactivated");
    }

    // Validate password
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      log.warn("Invalid password for user: {}", request.getLogin());
      throw new UnauthorizedException("Invalid credentials");
    }

    // Generate JWT token
    String token = jwtService.generateToken(user);

    log.info("User authenticated successfully: {} with role: {}",
      user.getLogin(), user.getRole());

    return LoginResponse.of(token, user);
  }

  /**
   * Validate user credentials (without generating token)
   * @param login user login
   * @param password user password
   * @return User entity if valid
   */
  public User validateCredentials(String login, String password) {
    User user = userRepository.findByLogin(login)
      .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

    if (!user.isActive()) {
      throw new UnauthorizedException("User account is deactivated");
    }

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new UnauthorizedException("Invalid credentials");
    }

    return user;
  }

  /**
   * Check if user account is active
   * @param user the user to check
   * @return true if active, false otherwise
   */
  public boolean isUserActive(User user) {
    return user != null && user.isActive();
  }
}
