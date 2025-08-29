package game

import (
	"world-quiz/internal/entities"
	"world-quiz/internal/filter"
)

// NOTE: Instead of int in the map, replace it later with a user id
type GameManager struct {
	Places []entities.Place
	Games  map[int]*Game
}

func (g *GameManager) NewGame(category entities.Category, tags []entities.Tag) int {
	filtered := filter.Filter(&g.Places, category, tags)
	g.Games[1] = &Game{Category: category, Places: filtered}
	return 1
}

func (g *GameManager) GetGame(id int) *Game {
	return g.Games[id]
}
