package game

import (
	"errors"
	"github.com/google/uuid"
	"world-quiz/internal/entities"
	"world-quiz/internal/filter"
)

type Manager struct {
	Places *[]entities.Place
	Games  map[uuid.UUID]entities.Game
}

func (m *Manager) CreateGame(c []entities.Category, t []entities.Tag) (entities.Game, error) {
	if !validRequest(c, t) {
		return entities.Game{}, errors.New("Not valid Category or Tag")
	}
	cards := filter.Filter(m.Places, c, t)
	gameId := uuid.New()
	game := entities.Game{Id: gameId, Cards: cards}
	m.Games[gameId] = game
	return game, nil
}

func NewManager(p *[]entities.Place) *Manager {
	return &Manager{Places: p, Games: map[uuid.UUID]entities.Game{}}
}

func validRequest(c []entities.Category, t []entities.Tag) bool {
	if len(c) == 0 {
		return false
	}
	for _, category := range c {
		if !category.Valid() {
			return false
		}
	}
	for _, tag := range t {
		if !tag.Valid() {
			return false
		}
	}
	return true
}
