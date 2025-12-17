package com.logistics.parcelandcarrier.dto.request;

import com.logistics.parcelandcarrier.enums.PackageType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for package creation and update
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageRequest {

  @NotNull(message = "Package type is required")
  private PackageType type;

  @NotNull(message = "Weight is required")
  @Positive(message = "Weight must be positive")
  @DecimalMax(value = "1000.0", message = "Weight cannot exceed 1000 kg")
  private Double weight;

  @NotBlank(message = "Destination address is required")
  @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
  private String destinationAddress;

  // For FRAGILE packages
  @Size(max = 1000, message = "Handling instructions cannot exceed 1000 characters")
  private String handlingInstructions;

  // For REFRIGERATED packages
  @DecimalMin(value = "-30.0", message = "Minimum temperature must be at least -30°C")
  @DecimalMax(value = "30.0", message = "Minimum temperature cannot exceed 30°C")
  private Double minTemperature;

  @DecimalMin(value = "-30.0", message = "Maximum temperature must be at least -30°C")
  @DecimalMax(value = "30.0", message = "Maximum temperature cannot exceed 30°C")
  private Double maxTemperature;

  /**
   * Check if this is a fragile package
   */
  public boolean isFragile() {
    return type != null && type.isFragile();
  }

  /**
   * Check if this is a refrigerated package
   */
  public boolean isRefrigerated() {
    return type != null && type.isRefrigerated();
  }

  /**
   * Validate business rules
   */
  public void validate() {
    // Validate fragile package must have handling instructions
    if (isFragile() && (handlingInstructions == null || handlingInstructions.trim().isEmpty())) {
      throw new IllegalArgumentException("Fragile packages must have handling instructions");
    }

    // Validate refrigerated package must have temperature range
    if (isRefrigerated()) {
      if (minTemperature == null || maxTemperature == null) {
        throw new IllegalArgumentException("Refrigerated packages must have temperature range");
      }
      if (minTemperature >= maxTemperature) {
        throw new IllegalArgumentException("Minimum temperature must be less than maximum temperature");
      }
    }
  }
}
