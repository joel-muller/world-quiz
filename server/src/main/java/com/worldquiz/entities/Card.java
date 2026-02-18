/* (C)2026 */
package com.worldquiz.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Card(
        int placeId,
        Category category,
        String front,
        String infoFront,
        String back,
        String infoBack) {}
