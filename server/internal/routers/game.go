package internal

import (
	"world-quiz/internal/entities"
	"world-quiz/internal/game"
)

func GameRouter(manager *game.Manager) {
	handlePost("/game", func(req entities.RequestGameStart) (entities.Game, error) {
		return manager.CreateGame(req)
	})

	handlePost("/game/finish", func(req entities.RequestGameFinish) (entities.GameStats, error) {
		return manager.FinishGame(req)
	})
}
