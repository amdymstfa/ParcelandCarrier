package com.logistics.parcelandcarrier.security;

import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Loading user by username: {}", username);

    User user = userRepository.findByLogin(username)
      .orElseThrow(() -> new UsernameNotFoundException(
        "User not found with login: " + username
      ));

    // Check if user is active
    if (!user.isActive()) {
      throw new UsernameNotFoundException(
        "User account is deactivated: " + username
      );
    }

    // Convert to Spring Security UserDetails
    return org.springframework.security.core.userdetails.User.builder()
      .username(user.getLogin())
      .password(user.getPassword())
      .authorities(Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
      ))
      .accountExpired(false)
      .accountLocked(false)
      .credentialsExpired(false)
      .disabled(!user.isActive())
      .build();
  }

  /**
   * Load user entity by username
   * @param username the username
   * @return User entity
   */
  public User loadUserEntityByUsername(String username) {
    return userRepository.findByLogin(username)
      .orElseThrow(() -> new UsernameNotFoundException(
        "User not found with login: " + username
      ));
  }
}

