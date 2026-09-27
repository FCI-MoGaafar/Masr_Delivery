package org.example;

public class InsufficientWalletException extends PlatformException{
    public InsufficientWalletException(String message) {
        super(message);
    }
}
