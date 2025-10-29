package internal

import (
	"world-quiz/internal/entities"
	"world-quiz/internal/game"
)

func GameRouter(manager *game.Manager) {
	handlePost("/game", func(req entities.RequestGame) (entities.Game, error) {
		return manager.CreateGame(req)
	})

	handlePost("/game/finish", func(req entities.RequestGameStats) (entities.GameStats, error) {
		return manager.FinishGame(req)
	})
}
