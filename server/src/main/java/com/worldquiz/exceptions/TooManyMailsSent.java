/* (C)2026 */
package com.worldquiz.exceptions;

public class TooManyMailsSent extends RuntimeException {
    public TooManyMailsSent(String message) {
        super(message);
    }
}
