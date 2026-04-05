/* (C)2026 */
package com.worldquiz.reader;

import static org.junit.jupiter.api.Assertions.*;

import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlaceReaderTest {

    private PlaceReader reader;

    @BeforeEach
    void setUp() {
        reader = new PlaceReader("dummyDir", 100);
    }

    @Test
    void testGetFlagOrMap_withQuotedString() {
        String input = "\"<img src='flag.svg' />\"";
        assertEquals("<img src='flag.svg' />", reader.getFlagOrMap(input));
    }

    @Test
    void testGetFlagOrMap_withoutQuotes() {
        String input = "<img src='flag.svg' />";
        assertNull(reader.getFlagOrMap(input));
    }

    @Test
    void testGetTags_singleKnownTag() {
        List<Tag> tags = reader.getTags("Europe");
        assertEquals(1, tags.size());
        assertEquals(Tag.EUROPE, tags.get(0));
    }

    @Test
    void testGetTags_multipleKnownTags() {
        List<Tag> tags = reader.getTags("Europe, Asia, Caribbean");
        assertTrue(tags.containsAll(Arrays.asList(Tag.EUROPE, Tag.ASIA, Tag.CARIBBEAN)));
        assertEquals(3, tags.size());
    }

    @Test
    void testGetTags_unknownTagIgnored() {
        List<Tag> tags = reader.getTags("Europe, Mars");
        assertEquals(1, tags.size());
        assertEquals(Tag.EUROPE, tags.get(0));
    }

    @Test
    void testGetTags_emptyInput() {
        List<Tag> tags = reader.getTags("");
        assertTrue(tags.isEmpty());
    }

    @Test
    void testReadMain_createsBuildersCorrectly() {
        List<String[]> lines = new ArrayList<>();
        lines.add(
                new String[] {"England", "\"flag1\"", "\"map1\"", "EU", "Europe, Sovereign_State"});
        lines.add(new String[] {"France", "\"flag2\"", "\"map2\"", "FR", "Europe"});

        Map<String, Place.PlaceBuilder> builders = reader.readMain(lines);

        assertEquals(2, builders.size());
        Place.PlaceBuilder englandBuilder = builders.get("England");
        assertNotNull(englandBuilder);
        Place england = englandBuilder.build();
        assertEquals(100, england.id());
        assertEquals("England", england.name());
        assertEquals("EU", england.regionCode());
        assertEquals("flag1", england.flag());
        assertEquals("map1", england.maps());
        assertTrue(england.tags().contains(Tag.EUROPE));
        assertTrue(england.tags().contains(Tag.SOVEREIGN_STATE));

        Place france = builders.get("France").build();
        assertEquals(101, france.id());
    }

    @Test
    void testUpdatePlaceBuilder_appliesSetter() {
        Place.PlaceBuilder builder = Place.builder().name("England");
        Map<String, Place.PlaceBuilder> builders = new HashMap<>();
        builders.put("England", builder);

        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] {"England", "Info about England"});
        reader.updatePlaceBuilder(lines, builders, Place.PlaceBuilder::placeInfo);

        assertEquals("Info about England", builder.build().placeInfo());
    }

    @Test
    void testUpdatePlaceBuilder_nonExistingKey() {
        Place.PlaceBuilder builder = Place.builder().name("England");
        Map<String, Place.PlaceBuilder> builders = new HashMap<>();
        builders.put("England", builder);

        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] {"France", "Info about France"});
        reader.updatePlaceBuilder(lines, builders, Place.PlaceBuilder::placeInfo);

        assertNull(builder.build().placeInfo());
    }

    @Test
    void testUpdatePlaceBuilder_emptyLines() {
        Place.PlaceBuilder builder = Place.builder().name("England");
        Map<String, Place.PlaceBuilder> builders = new HashMap<>();
        builders.put("England", builder);

        reader.updatePlaceBuilder(Collections.emptyList(), builders, Place.PlaceBuilder::placeInfo);
        assertNull(builder.build().placeInfo());
    }
}
