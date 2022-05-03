package com.tourguide.rewardservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DataAlreadyExistException extends RuntimeException {
  public DataAlreadyExistException(String message) {
    super(message);
  }
}
