/* (C)2026 */
package com.worldquiz.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    MAP_NAME,
    FLAG_NAME,
    NAME_CAPITAL,
    CAPITAL_NAME;

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }
}
