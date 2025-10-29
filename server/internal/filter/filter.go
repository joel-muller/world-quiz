package filter

import (
	"math/rand"
	"slices"
	"time"
	"world-quiz/internal/entities"
)

func Filter(places *[]entities.Place, request entities.RequestGame) []entities.Card {
	filtered := filterTags(places, request.Tags)
	cards := filterCategory(&filtered, request.Categories)
	shuffleCards(&cards)
	if request.Number != nil {
		number := *request.Number
		if len(cards) < number {
			return cards
		}
		return cards[:number]
	}
	return cards
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

func shuffleCards(cards *[]entities.Card) {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))
	r.Shuffle(len(*cards), func(i, j int) {
		(*cards)[i], (*cards)[j] = (*cards)[j], (*cards)[i]
	})
}
