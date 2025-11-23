package com.logistics.parcelandcarrier.dto.response;

import com.logistics.parcelandcarrier.enums.Role;
import com.logistics.parcelandcarrier.enums.Specialty;
import com.logistics.parcelandcarrier.enums.TransporterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String login;
    private Role role;
    private boolean active;
    private Specialty specialty;
    private TransporterStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}