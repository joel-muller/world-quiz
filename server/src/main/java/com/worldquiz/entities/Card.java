package com.worldquiz.entities;

public record Card(int placeId, Category category, String front, String infoFront, String back, String infoBack) {
}
