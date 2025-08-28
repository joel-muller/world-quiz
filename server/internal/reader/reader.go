package reader

import (
	"encoding/csv"
	"io"
	"log"
	"os"
	"regexp"
	"strings"
	"world-quiz/internal/entities"
)

func Read(path string) ([]entities.Place, error) {
	places := []entities.Place{}
	if err := readAndApply(path+"main.csv", &places, applyLineMain); err != nil {
		return []entities.Place{}, err
	}
	addIds(&places)
	if err := readAndApply(path+"capital.csv", &places, applyLineCapital); err != nil {
		return []entities.Place{}, err
	}
	if err := readAndApply(path+"capital_info.csv", &places, applyLineCapitalInfo); err != nil {
		return []entities.Place{}, err
	}
	if err := readAndApply(path+"country_info.csv", &places, applyLineCountryInfo); err != nil {
		return []entities.Place{}, err
	}
	return places, nil
}

func readAndApply(filename string, places *[]entities.Place, operator func([]string, *[]entities.Place)) error {
	dat, err := os.ReadFile(filename)
	if err != nil {
		return err
	}
	r := csv.NewReader(strings.NewReader(string(dat)))

	if _, err := r.Read(); err != nil {
		return err
	}

	for {
		record, err := r.Read()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Fatal(err)
		}
		operator(record, places)
	}
	return nil
}

func addIds(places *[]entities.Place) {
	for i := range *places {
		(*places)[i].Id = i
	}
}

func applyLineMain(record []string, places *[]entities.Place) {
	place := entities.Place{Name: extractValue(record[0]), Flag: extractFlagOrMaps(record[1]), Maps: extractFlagOrMaps(record[2]), RegionCode: extractValue(record[3]), Tags: extractTags(record[4])}
	*places = append(*places, place)
}

func applyLineCapital(record []string, places *[]entities.Place) {
	for i := range *places {
		if (*places)[i].Name == record[0] {
			(*places)[i].Capital = record[1]
		}
	}
}

func applyLineCapitalInfo(record []string, places *[]entities.Place) {
	for i := range *places {
		if (*places)[i].Name == record[0] {
			(*places)[i].CapitalInfo = record[1]
		}
	}
}

func applyLineCountryInfo(record []string, places *[]entities.Place) {
	for i := range *places {
		if (*places)[i].Name == record[0] {
			(*places)[i].PlaceInfo = record[1]
		}
	}
}

func extractValue(s string) string {
	for _, v := range s {
		if v != ' ' {
			return s
		}
	}
	return ""
}

func extractFlagOrMaps(s string) string {
	re := regexp.MustCompile(`"(.*?)"`)
	return strings.ReplaceAll(re.FindString(s), `"`, "")
}

func extractTags(tagsString string) []entities.Tag {
	tagsList := map[string]entities.Tag{
		"Europe":          entities.Europe,
		"Asia":            entities.Asia,
		"Oceania":         entities.Oceania,
		"North_America":   entities.North_America,
		"South_America":   entities.South_America,
		"Africa":          entities.Africa,
		"Oceans+Seas":     entities.OceansSeas,
		"Continents":      entities.Continents,
		"Sovereign_State": entities.Sovereign_States,
		"Mediterranean":   entities.Mediterranean,
		"European_Union":  entities.European_Union,
		"Middle_East":     entities.Middle_East,
		"East_Africa":     entities.East_Africa,
		"Southeast_Asia":  entities.Southeast_Asia,
		"Caribbean":       entities.Caribbean,
	}
	tags := []entities.Tag{}
	for key, value := range tagsList {
		if strings.Contains(tagsString, key) {
			tags = append(tags, value)
		}
	}
	return tags
}
