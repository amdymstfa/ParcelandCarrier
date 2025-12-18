package com.logistics.parcelandcarrier.config;

import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.enums.Role;
import com.logistics.parcelandcarrier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    userRepository.findByLogin("admin").ifPresent(userRepository::delete);

    User admin = User.builder()
      .login("admin")
      .password(passwordEncoder.encode("admin123"))
      .role(Role.ADMIN)
      .active(true)
      .createdAt(LocalDateTime.now())
      .build();

    userRepository.save(admin);
    System.out.println("✅ DEFAULT ADMIN CREATED: admin / admin123");
  }
}
