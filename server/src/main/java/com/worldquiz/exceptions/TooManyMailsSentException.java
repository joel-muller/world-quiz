/* (C)2026 */
package com.worldquiz.exceptions;

public class TooManyMailsSentException extends RuntimeException {
    public TooManyMailsSentException(String message) {
        super(message);
    }
}
