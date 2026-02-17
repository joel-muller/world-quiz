package com.worldquiz.reader;

import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class PlaceBuilder {
    private int id = -1;
    private String name;
    private String placeInfo;
    private String capital;
    private String capitalInfo;
    private String regionCode;
    private String maps;
    private String flag;
    private String flagInfo;
    private List<Tag> tags = new ArrayList<>();


    private static String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }


    public PlaceBuilder id(int id) {
        this.id = id;
        return this;
    }

    public PlaceBuilder name(String name) {
        this.name = emptyToNull(name);
        return this;
    }

    public PlaceBuilder placeInfo(String placeInfo) {
        this.placeInfo = emptyToNull(placeInfo);
        return this;
    }

    public PlaceBuilder capital(String capital) {
        this.capital = emptyToNull(capital);
        return this;
    }

    public PlaceBuilder capitalInfo(String capitalInfo) {
        this.capitalInfo = emptyToNull(capitalInfo);
        return this;
    }

    public PlaceBuilder regionCode(String regionCode) {
        this.regionCode = emptyToNull(regionCode);
        return this;
    }

    public PlaceBuilder maps(String maps) {
        this.maps = emptyToNull(maps);
        return this;
    }

    public PlaceBuilder flag(String flag) {
        this.flag = emptyToNull(flag);
        return this;
    }

    public PlaceBuilder flagInfo(String flagInfo) {
        this.flagInfo = emptyToNull(flagInfo);
        return this;
    }

    public PlaceBuilder tags(List<Tag> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        return this;
    }

    public PlaceBuilder addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Place build() {
        if (this.id == -1 || this.name == null) {
            throw new RuntimeException("Id or name of Place is not set");
        }
        return new Place(
                this.id,
                this.name,
                this.placeInfo,
                this.capital,
                this.capitalInfo,
                this.regionCode,
                this.maps,
                this.flag,
                this.flagInfo,
                List.copyOf(this.tags)
        );
    }
}
