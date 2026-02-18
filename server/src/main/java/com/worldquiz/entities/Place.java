/* (C)2026 */
package com.worldquiz.entities;

import java.util.List;

public record Place(
        int id,
        String name,
        String placeInfo,
        String capital,
        String capitalInfo,
        String regionCode,
        String maps,
        String flag,
        String flagInfo,
        List<Tag> tags) {

    public String nameWithCapital() {
        return this.capital() != null ? this.name() + " (" + this.capital() + ")" : this.name();
    }
}
