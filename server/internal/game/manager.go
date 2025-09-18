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

func (m *Manager) CreateGame(req entities.GameRequest) (entities.Game, error) {
	if !validRequest(req) {
		return entities.Game{}, errors.New("Not valid Category or Tag")
	}
	cards := filter.Filter(m.Places, req)
	gameId := uuid.New()
	game := entities.Game{Id: gameId, Cards: cards}
	m.Games[gameId] = game
	return game, nil
}

func NewManager(p *[]entities.Place) *Manager {
	return &Manager{Places: p, Games: map[uuid.UUID]entities.Game{}}
}

func validRequest(req entities.GameRequest) bool {
	if len(req.Categories) == 0 || len(req.Tags) == 0 {
		return false
	}
	if req.Number != nil && *req.Number <= 0 {
		return false
	}
	for _, category := range req.Categories {
		if !category.Valid() {
			return false
		}
	}
	for _, tag := range req.Tags {
		if !tag.Valid() {
			return false
		}
	}
	return true
}
