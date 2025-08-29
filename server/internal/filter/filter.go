package filter

import (
	"slices"
	"world-quiz/internal/entities"
)

func Filter(places *[]entities.Place, category entities.Category, tags []entities.Tag) []entities.Place {
	categoryFiltered := filterCategory(places, category)
	return filterTags(categoryFiltered, tags)

}

func filterCategory(places *[]entities.Place, category entities.Category) []entities.Place {
	filtered := []entities.Place{}
	for _, p := range *places {
		if !isValid(p) {
			continue
		}
		if (category == entities.NameCapital || category == entities.CapitalName) && p.Capital != "" {
			filtered = append(filtered, p)
		}
		if category == entities.FlagName && p.Flag != "" {
			filtered = append(filtered, p)
		}
		if category == entities.MapName && p.Maps != "" {
			filtered = append(filtered, p)
		}
	}
	return filtered
}

func filterTags(places []entities.Place, tags []entities.Tag) []entities.Place {
	filtered := []entities.Place{}
	for _, p := range places {
		if inTags(p, tags) {
			filtered = append(filtered, p)
		}
	}
	return filtered
}

func inTags(place entities.Place, tags []entities.Tag) bool {
	if len(tags) == 0 {
		return true
	}
	for _, t := range tags {
		if slices.Contains(place.Tags, t) {
			return true
		}
	}
	return false
}

func isValid(place entities.Place) bool {
	if place.Name != "" && place.Id != 0 {
		return true
	}
	return false
}
