package reader

import (
	"encoding/csv"
	"io"
	"os"
	"regexp"
	"strings"
	"world-quiz/internal/entities"
)

type fileProcessor struct {
	name     string
	operator func(int, []string, *[]entities.Place)
}

var fileProcessors = []fileProcessor{
	{"main.csv", applyLineMain},
	{"capital.csv", applyLineCapital},
	{"capital_info.csv", applyLineCapitalInfo},
	{"country_info.csv", applyLineCountryInfo},
	{"flag_similarity.csv", applyFlagSimilarity},
}

var tagsList = map[string]entities.Tag{
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

func Read() ([]entities.Place, error) {
	places := []entities.Place{}
	for _, f := range fileProcessors {
		reader, file, err := getReader(f.name)
		if err != nil {
			return []entities.Place{}, err
		}
		if err := readAndApply(reader, &places, f.operator); err != nil {
			return []entities.Place{}, err
		}
		file.Close()
	}
	return places, nil
}

func getReader(filename string) (*csv.Reader, *os.File, error) {
	path := "data/"
	file, err := os.Open(path + filename)
	if err != nil {
		return nil, nil, err
	}
	csvreader := csv.NewReader(file)
	// Read the first line
	if _, err := csvreader.Read(); err != nil {
		return nil, nil, err
	}
	return csvreader, file, nil
}

func readAndApply(r *csv.Reader, places *[]entities.Place, operator func(int, []string, *[]entities.Place)) error {
	count := 0
	for {
		record, err := r.Read()
		if err == io.EOF {
			break
		}
		if err != nil {
			return err
		}
		operator(count, record, places)
		count++
	}
	return nil
}

func applyLineMain(count int, record []string, places *[]entities.Place) {
	place := entities.Place{Id: count + 1, Name: strings.TrimSpace(record[0]), Flag: extractFlagOrMaps(record[1]), Maps: extractFlagOrMaps(record[2]), RegionCode: strings.TrimSpace(record[3]), Tags: extractTags(record[4])}
	*places = append(*places, place)
}

func getPlaceByName(name string, places *[]entities.Place) *entities.Place {
	for i := range *places {
		if (*places)[i].Name == name {
			return &(*places)[i]
		}
	}
	return nil
}

func applyLineCapital(count int, record []string, places *[]entities.Place) {
	if p := getPlaceByName(record[0], places); p != nil {
		p.Capital = record[1]
	}
}

func applyLineCapitalInfo(count int, record []string, places *[]entities.Place) {
	if p := getPlaceByName(record[0], places); p != nil {
		p.CapitalInfo = record[1]
	}
}

func applyLineCountryInfo(count int, record []string, places *[]entities.Place) {
	if p := getPlaceByName(record[0], places); p != nil {
		p.PlaceInfo = record[1]
	}
}

func applyFlagSimilarity(count int, record []string, places *[]entities.Place) {
	if p := getPlaceByName(record[0], places); p != nil {
		p.FlagInfo = record[1]
	}
}

func extractFlagOrMaps(s string) string {
	re := regexp.MustCompile(`"(.*?)"`)
	return strings.ReplaceAll(re.FindString(s), `"`, "")
}

func extractTags(tagsString string) []entities.Tag {
	tags := []entities.Tag{}
	for key, value := range tagsList {
		if strings.Contains(tagsString, key) {
			tags = append(tags, value)
		}
	}
	return tags
}
