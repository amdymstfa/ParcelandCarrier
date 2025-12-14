package com.logistics.parcelandcarrier.exception;

import com.logistics.parcelandcarrier.enums.TransporterStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when transporter is not available for assignment
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class TransporterUnavailableException extends RuntimeException {

  private final String transporterId;
  private final TransporterStatus currentStatus;

  public TransporterUnavailableException(String transporterId, TransporterStatus currentStatus) {
    super(String.format(
      "Transporter %s is not available. Current status: %s",
      transporterId, currentStatus
    ));
    this.transporterId = transporterId;
    this.currentStatus = currentStatus;
  }
}
