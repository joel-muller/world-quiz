/* (C)2026 */
package com.worldquiz.exceptions;

public class QuizBelongsNotToUserException extends RuntimeException {
    public QuizBelongsNotToUserException(String message) {
        super(message);
    }
}
