package main

import (
	"log"
	"net/http"
	"world-quiz/internal/game"
	"world-quiz/internal/reader"
	"world-quiz/internal/routers"
)

func main() {
	places, err := reader.Read()
	if err != nil {
		log.Fatal(err)
	}
	manager := game.NewManager(&places)

	log.Println("World Quiz server is ready")

	internal.GameRouter(manager)
	internal.AuthenticationRouter()

	log.Println("Server running on http://localhost:8080")
	http.ListenAndServe(":8080", nil)
}
