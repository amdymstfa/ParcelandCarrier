package com.logistics.parcelandcarrier.exception;

import com.logistics.parcelandcarrier.enums.PackageType;
import com.logistics.parcelandcarrier.enums.Specialty;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when transporter specialty doesn't match package type
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpecialtyIncompatibleException extends RuntimeException {

  private final PackageType requiredPackageType;
  private final Specialty transporterSpecialty;

  public SpecialtyIncompatibleException(PackageType requiredPackageType, Specialty transporterSpecialty) {
    super(String.format(
      "Transporter with specialty %s cannot handle package of type %s",
      transporterSpecialty, requiredPackageType
    ));
    this.requiredPackageType = requiredPackageType;
    this.transporterSpecialty = transporterSpecialty;
  }
}
