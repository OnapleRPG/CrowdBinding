package com.onaple.crowdbinding.exceptions;

public class ExpiredInvitationException extends Throwable {
    public ExpiredInvitationException(String errorMessage) {
        super(errorMessage);
    }
}
