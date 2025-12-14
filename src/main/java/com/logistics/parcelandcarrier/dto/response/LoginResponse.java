package com.logistics.parcelandcarrier.dto.response;

import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  private String token;
  private String login;
  private Role role;
  private String userId;

  /**
   * Factory method to create LoginResponse from token and user
   */
  public static LoginResponse of(String token, User user) {
    return LoginResponse.builder()
      .token(token)
      .login(user.getLogin())
      .role(user.getRole())
      .userId(user.getId())
      .build();
  }
}
