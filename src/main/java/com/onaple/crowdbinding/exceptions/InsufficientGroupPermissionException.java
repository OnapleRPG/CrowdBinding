package com.onaple.crowdbinding.exceptions;

public class InsufficientGroupPermissionException extends Throwable {
    public InsufficientGroupPermissionException(String errorMessage) {
        super(errorMessage);
    }
}
