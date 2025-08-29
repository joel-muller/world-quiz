package main

import (
	"fmt"
	"log"
	"world-quiz/internal/entities"
	"world-quiz/internal/game"
	"world-quiz/internal/reader"
)

func main() {
	places, err := reader.Read()
	if err != nil {
		log.Fatal(err)
	}

	gameManager := game.GameManager{Places: places, Games: map[int]*game.Game{}}
	gameId := gameManager.NewGame(entities.MapName, []entities.Tag{entities.Africa})

	for gameManager.GetGame(gameId).Active() {
		fmt.Println(gameManager.GetGame(gameId).CurrentCard().Front)
		var i, j string
		fmt.Scan(&j)
		fmt.Println(gameManager.GetGame(gameId).CurrentCard().Back)
		fmt.Scan(&i)
		if i == "y" {
			gameManager.GetGame(gameId).Guess(true)
		} else {
			gameManager.GetGame(gameId).Guess(false)
		}
	}
}
