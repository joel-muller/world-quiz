/* (C)2026 */
package com.worldquiz.dto;

import java.util.List;
import java.util.UUID;

public record GameStatDto(UUID id, String info, List<CardStatDto> cards) {}
