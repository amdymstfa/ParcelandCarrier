package com.logistics.parcelandcarrier.dto.request ;

import com.logistics.parcelandcarrier.enums.Specialty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creation and update transporter
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransporterRequest {

    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Login can only contain letters, numbers and underscores")
    private String login ;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 20, message = "Password must contains between 5 and 20 characters")
    private String password ;

    @NotBlank(message = "Specialty is required")
    private Specialty specialty ;
}