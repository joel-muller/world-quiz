package com.worldquiz.reader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceReader {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceReader.class);
    private final String baseDir;
    private final int firstId;

    public PlaceReader(String baseDir, int firstId) {
        this.baseDir = baseDir;
        this.firstId = firstId;
    }

    public List<Place> read() {
        Map<String, PlaceBuilder> builders = this.readMain(this.readCsv("main.csv"));
        updatePlaceBuilder(this.readCsv("country_info.csv"), builders, PlaceBuilder::placeInfo);
        updatePlaceBuilder(this.readCsv("capital.csv"), builders, PlaceBuilder::capital);
        updatePlaceBuilder(this.readCsv("capital_info.csv"), builders, PlaceBuilder::capitalInfo);
        updatePlaceBuilder(this.readCsv("flag_similarity.csv"), builders, PlaceBuilder::flagInfo);
        return builders.values().stream().map(PlaceBuilder::build).toList();
    }

    private List<String[]> readCsv(String fileName) {
        List<String[]> lines = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(this.baseDir + "/" + fileName))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                lines.add(nextLine);
            }
        } catch (IOException e) {
            LOG.error("Error occurred while reading the file: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            LOG.error("Error occurred while validating the file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return lines;
    }

    private Map<String, PlaceBuilder> readMain(List<String[]> lines) {
        Map<String, PlaceBuilder> builders = new HashMap<>();
        for (int i = 0; i<lines.size(); i++) {
            String[] line = lines.get(i);
            String name = line[0];
            PlaceBuilder builder = new PlaceBuilder()
                    .id(this.firstId + i)
                    .name(name)
                    .regionCode(line[3])
                    .flag(getFlagOrMap(line[1]))
                    .maps(getFlagOrMap(line[2]))
                    .tags(getTags(Arrays.toString(Arrays.copyOfRange(line, 4, line.length))));
            builders.put(name, builder);
        }
        return builders;
    }

    private void updatePlaceBuilder(List<String[]> lines, Map<String, PlaceBuilder> builders, BiConsumer<PlaceBuilder, String> setter) {
        for (String[] line : lines) {
            PlaceBuilder builder = builders.get(line[0]);
            if (builder != null) {
                setter.accept(builder, line[1]);
            }
        }
    }

    private String getFlagOrMap(String input) {
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private List<Tag> getTags(String input) {
        Map<String, Tag> tagsName = Map.ofEntries(
                Map.entry("Europe", Tag.EUROPE),
                Map.entry("Asia", Tag.ASIA),
                Map.entry("Oceania", Tag.OCEANIA),
                Map.entry("North_America", Tag.NORTH_AMERICA),
                Map.entry("South_America", Tag.SOUTH_AMERICA),
                Map.entry("Africa", Tag.AFRICA),
                Map.entry("Oceans+Seas", Tag.OCEANS_AND_SEAS),
                Map.entry("Continents", Tag.CONTINENTS),
                Map.entry("Sovereign_State", Tag.SOVEREIGN_STATE),
                Map.entry("Mediterranean", Tag.MEDITERRANEAN),
                Map.entry("European_Union", Tag.EUROPEAN_UNION),
                Map.entry("Middle_East", Tag.MIDDLE_EAST),
                Map.entry("East_Africa", Tag.EAST_AFRICA),
                Map.entry("Southeast_Asia", Tag.SOUTHEAST_ASIA),
                Map.entry("Caribbean", Tag.CARIBBEAN)
        );
        return tagsName.keySet().stream()
                .filter(input::contains)
                .map(tagsName::get)
                .toList();
    }

    public static void main(String[] args) {
        PlaceReader reader = new PlaceReader("data", 10000);
        List<Place> places = reader.read();
        for (Place place: places) {
            System.out.println(place.capital());
        }

    }
}
