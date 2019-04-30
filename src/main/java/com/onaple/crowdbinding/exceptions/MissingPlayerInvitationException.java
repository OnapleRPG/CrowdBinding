package com.onaple.crowdbinding.exceptions;

public class MissingPlayerInvitationException extends Exception {
    public MissingPlayerInvitationException(String errorMessage) {
        super(errorMessage);
    }
}
