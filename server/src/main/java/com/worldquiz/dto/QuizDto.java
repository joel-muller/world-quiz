/* (C)2026 */
package com.worldquiz.dto;

import java.util.List;
import java.util.UUID;

public record QuizDto(UUID id, List<CardDto> cards) {}
