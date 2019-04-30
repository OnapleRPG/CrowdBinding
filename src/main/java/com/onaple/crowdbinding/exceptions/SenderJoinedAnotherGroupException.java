package com.onaple.crowdbinding.exceptions;

public class SenderJoinedAnotherGroupException extends Throwable {
    public SenderJoinedAnotherGroupException(String errorMessage) {
        super(errorMessage);
    }
}
