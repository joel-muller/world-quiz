package filter

import (
	"slices"
	"world-quiz/internal/entities"
)

func Filter(places *[]entities.Place, categories []entities.Category, tags []entities.Tag) []entities.Card {
	filteredTags := filterTags(places, tags)
	return filterCategory(&filteredTags, categories)
}

func filterCategory(places *[]entities.Place, categories []entities.Category) []entities.Card {
	filtered := []entities.Card{}
	for _, c := range categories {
		for _, p := range *places {
			if !isValid(p) {
				continue
			}
			if (c == entities.NameCapital || c == entities.CapitalName) && p.Capital != "" {
				filtered = append(filtered, p.GetCard(c))
			}
			if c == entities.FlagName && p.Flag != "" {
				filtered = append(filtered, p.GetCard(c))
			}
			if c == entities.MapName && p.Maps != "" {
				filtered = append(filtered, p.GetCard(c))
			}
		}
	}
	return filtered
}

func filterTags(places *[]entities.Place, tags []entities.Tag) []entities.Place {
	filtered := []entities.Place{}
	for _, p := range *places {
		if inTags(p, tags) || len(tags) == 0 {
			filtered = append(filtered, p)
		}
	}
	return filtered
}

func inTags(place entities.Place, tags []entities.Tag) bool {
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
