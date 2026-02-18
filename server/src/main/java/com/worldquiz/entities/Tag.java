/* (C)2026 */
package com.worldquiz.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Tag {
    EUROPE,
    ASIA,
    OCEANIA,
    NORTH_AMERICA,
    SOUTH_AMERICA,
    AFRICA,
    OCEANS_AND_SEAS,
    CONTINENTS,
    SOVEREIGN_STATE,
    MEDITERRANEAN,
    EUROPEAN_UNION,
    MIDDLE_EAST,
    EAST_AFRICA,
    SOUTHEAST_ASIA,
    CARIBBEAN;

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
