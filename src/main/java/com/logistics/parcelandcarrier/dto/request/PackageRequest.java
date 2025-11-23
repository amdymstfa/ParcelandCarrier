package com.logistics.parcelandcarrier.dto.request ;

import com.logistics.parcelandcarrier.enums.PackageType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for package creation and update
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageRequest {

    @NotBlank(message = "Package type is required")
    private PackageType type ;

    @NotBlank(message = "Destination address is required")
    @Size(min = 10, max = 500, message = "Address must contains between 10 and 500 caracters")
    private String destinationAddress ;

    @Size(max = 1000 , message = "Handling instructions cannot exceed 1000 caracters")
    private String handlingInstructions ;

    @DecimalMin(value = "-30.0", message = "Minimum temperature must be at least -30°C")
    @DecimalMax(value = "30.0", message = "Minimum temperature cannot exceed 30°C")
    private Double minTemperature;

    @DecimalMin(value = "-30.0", message = "Maximum temperature must be at least -30°C")
    @DecimalMax(value = "30.0", message = "Maximum temperature cannot exceed 30°C")
    private Double maxTemperature;
}