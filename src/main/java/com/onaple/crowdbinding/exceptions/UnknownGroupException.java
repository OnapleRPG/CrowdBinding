package com.onaple.crowdbinding.exceptions;

public class UnknownGroupException extends Exception {
    public UnknownGroupException(String errorMessage) {
        super(errorMessage);
    }

    public UnknownGroupException() {
        super("Unknown group Id, maybe it no longer exists");
    }
}
