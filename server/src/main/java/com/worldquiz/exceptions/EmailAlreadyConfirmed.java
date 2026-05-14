/* (C)2026 */
package com.worldquiz.exceptions;

public class EmailAlreadyConfirmed extends RuntimeException {
    public EmailAlreadyConfirmed(String message) {
        super(message);
    }
}
