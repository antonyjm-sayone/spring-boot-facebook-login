package com.opensource.facebooklogin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenException extends RuntimeException{

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(String message){ super(message); }
}
