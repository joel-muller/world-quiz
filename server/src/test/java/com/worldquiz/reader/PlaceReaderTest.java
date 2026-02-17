package com.worldquiz.reader;

import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaceReaderTest {
    @TempDir
    Path tempDir;

    private void writeFile(String fileName, List<String> lines) throws IOException {
        Files.write(tempDir.resolve(fileName), lines);
    }

    private void createMinimalValidFiles() throws IOException {
        writeFile("main.csv", List.of(
                "France,\"info\"flagUrl\"info\",\"info\"mapUrl\"info\",EU,Europe,Sovereign_State"
        ));

        writeFile("country_info.csv", List.of(
                "France,Country info"
        ));

        writeFile("capital.csv", List.of(
                "France,Paris"
        ));

        writeFile("capital_info.csv", List.of(
                "France,Capital info"
        ));

        writeFile("flag_similarity.csv", List.of(
                "France,Similar flag"
        ));
    }

    @Test
    void shouldReadSinglePlaceSuccessfully() throws IOException {
        createMinimalValidFiles();

        PlaceReader reader = new PlaceReader(tempDir.toString(), 100);
        List<Place> places = reader.read();

        assertEquals(1, places.size());

        Place france = places.get(0);

        assertEquals("France", france.name());
        assertEquals(100, france.id());
        assertEquals("EU", france.regionCode());
        assertEquals("Paris", france.capital());
        assertEquals("flagUrl", france.flag());
        assertEquals("mapUrl", france.maps());

        assertTrue(france.tags().contains(Tag.EUROPE));
        assertTrue(france.tags().contains(Tag.SOVEREIGN_STATE));
    }

    @Test
    void shouldAssignIncrementingIds() throws IOException {
        writeFile("main.csv", List.of(
                "France,\"f1\",\"m1\",EU,Europe",
                "Germany,\"f2\",\"m2\",EU,Europe"
        ));

        writeFile("country_info.csv", List.of());
        writeFile("capital.csv", List.of());
        writeFile("capital_info.csv", List.of());
        writeFile("flag_similarity.csv", List.of());

        PlaceReader reader = new PlaceReader(tempDir.toString(), 500);
        List<Place> places = reader.read();

        assertEquals(2, places.size());
        assertEquals(500, places.get(0).id());
        assertEquals(501, places.get(1).id());
    }

    @Test
    void shouldReturnNullFlagWhenNoQuotesPresent() throws IOException {
        writeFile("main.csv", List.of(
                "France,flagWithoutQuotes,mapWithoutQuotes,EU,Europe"
        ));

        writeFile("country_info.csv", List.of());
        writeFile("capital.csv", List.of());
        writeFile("capital_info.csv", List.of());
        writeFile("flag_similarity.csv", List.of());

        PlaceReader reader = new PlaceReader(tempDir.toString(), 1);
        List<Place> places = reader.read();

        Place place = places.get(0);

        assertNull(place.flag());
        assertNull(place.maps());
    }

    @Test
    void shouldIgnoreUpdatesForUnknownPlace() throws IOException {
        writeFile("main.csv", List.of(
                "France,\"flag\",\"map\",EU,Europe"
        ));

        writeFile("country_info.csv", List.of(
                "Germany,Some info"
        ));

        writeFile("capital.csv", List.of());
        writeFile("capital_info.csv", List.of());
        writeFile("flag_similarity.csv", List.of());

        PlaceReader reader = new PlaceReader(tempDir.toString(), 1);
        List<Place> places = reader.read();

        assertEquals(1, places.size());
        assertNull(places.get(0).capital());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFileMissing() {
        PlaceReader reader = new PlaceReader(tempDir.toString(), 1);

        RuntimeException ex = assertThrows(RuntimeException.class, reader::read);
        assertNotNull(ex.getCause());
    }

    @Test
    void shouldParseMultipleTags() throws IOException {
        writeFile("main.csv", List.of(
                "Egypt,\"flag\",\"map\",AF,Africa,Middle_East"
        ));

        writeFile("country_info.csv", List.of());
        writeFile("capital.csv", List.of());
        writeFile("capital_info.csv", List.of());
        writeFile("flag_similarity.csv", List.of());

        PlaceReader reader = new PlaceReader(tempDir.toString(), 1);
        List<Place> places = reader.read();

        List<Tag> tags = places.get(0).tags();

        assertTrue(tags.contains(Tag.AFRICA));
        assertTrue(tags.contains(Tag.MIDDLE_EAST));
    }
}