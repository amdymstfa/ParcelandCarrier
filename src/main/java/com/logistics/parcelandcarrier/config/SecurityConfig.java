package com.logistics.parcelandcarrier.config;

import com.logistics.parcelandcarrier.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // Disable CSRF (stateless API with JWT)
      .csrf(AbstractHttpConfigurer::disable)

      // Configure CORS
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))

      // Configure session management (stateless)
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )

      // Configure authorization
      .authorizeHttpRequests(auth -> auth
        // Public endpoints
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
        .requestMatchers("/actuator/health").permitAll()

        // Admin endpoints
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Transporter endpoints
        .requestMatchers("/api/transporter/**").hasRole("TRANSPORTER")

        // All other requests must be authenticated
        .anyRequest().authenticated()
      )

      // Add JWT filter before UsernamePasswordAuthenticationFilter
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

      // Configure authentication provider
      .authenticationProvider(authenticationProvider());

    return http.build();
  }

  /**
   * Authentication provider configuration
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Password encoder bean (BCrypt)
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  /**
   * Authentication manager bean
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * CORS configuration
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allowed origins (configure according to your frontend)
    configuration.setAllowedOrigins(Arrays.asList(
      "http://localhost:3000",
      "http://localhost:4200",
      "http://localhost:8080"
    ));

    // Allowed methods
    configuration.setAllowedMethods(Arrays.asList(
      HttpMethod.GET.name(),
      HttpMethod.POST.name(),
      HttpMethod.PUT.name(),
      HttpMethod.PATCH.name(),
      HttpMethod.DELETE.name(),
      HttpMethod.OPTIONS.name()
    ));

    // Allowed headers
    configuration.setAllowedHeaders(List.of("*"));

    // Allow credentials
    configuration.setAllowCredentials(true);

    // Max age
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
