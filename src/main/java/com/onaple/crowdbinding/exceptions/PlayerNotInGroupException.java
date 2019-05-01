package com.onaple.crowdbinding.exceptions;

public class PlayerNotInGroupException extends Throwable {
    public PlayerNotInGroupException(String errorMessage) {
        super(errorMessage);
    }
}
