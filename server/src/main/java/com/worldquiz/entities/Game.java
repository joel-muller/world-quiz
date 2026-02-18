/* (C)2026 */
package com.worldquiz.entities;

import java.util.List;
import java.util.UUID;

public record Game(UUID id, List<Card> cards) {}
