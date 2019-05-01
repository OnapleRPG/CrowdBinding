package com.onaple.crowdbinding.exceptions;

public class PlayerAlreadyInAGroupException extends Throwable {
    public PlayerAlreadyInAGroupException(String errorMessage) {
        super(errorMessage);
    }
}
