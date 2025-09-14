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

func (m *Manager) CreateGame(c entities.Category, t []entities.Tag) (entities.Game, error) {
	if !validRequest(c, t) {
		return entities.Game{}, errors.New("Not valid Category or Tag")
	}
	places := filter.Filter(m.Places, c, t)
	cards := []entities.Card{}
	for _, p := range places {
		cards = append(cards, p.GetCard(c))
	}
	gameId := uuid.New()
	game := entities.Game{Id: gameId, Category: c, Cards: cards}
	m.Games[gameId] = game
	return game, nil
}

func NewManager(p *[]entities.Place) *Manager {
	return &Manager{Places: p, Games: map[uuid.UUID]entities.Game{}}
}

func validRequest(c entities.Category, t []entities.Tag) bool {
	for _, v := range t {
		if !v.Valid() {
			return false
		}
	}
	return c.Valid()
}
