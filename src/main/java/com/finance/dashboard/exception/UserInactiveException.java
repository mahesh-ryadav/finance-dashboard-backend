package com.finance.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserInactiveException extends RuntimeException {

    public UserInactiveException(String email) {
        super(String.format("User account with email '%s' is inactive. Please contact admin.", email));
    }

    public UserInactiveException() {
        super("Your account is inactive. Please contact admin.");
    }
}