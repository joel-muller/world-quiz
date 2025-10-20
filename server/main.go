package main

import (
	"encoding/json"
	"log"
	"net/http"
	"world-quiz/internal/entities"
	"world-quiz/internal/game"
	"world-quiz/internal/reader"
)

func main() {
	places, err := reader.Read()
	if err != nil {
		log.Fatal(err)
	}
	manager := game.NewManager(&places)

	log.Println("World Quiz server is ready")

	http.HandleFunc("/game", func(w http.ResponseWriter, r *http.Request) {
		// NOTE: When i want to test it locally uncomment this lines to enable
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")

		// Handle preflight requests
		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusOK)
			return
		}
		// NOTE: Here is the end of the uncommented lines

		if r.Method != http.MethodPost {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return
		}

		var req entities.RequestGameStart
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			http.Error(w, "Invalid JSON: "+err.Error(), http.StatusBadRequest)
			return
		}
		defer r.Body.Close()

		game, err := manager.CreateGame(req)
		if err != nil {
			http.Error(w, "Invalid Category or Tag: "+err.Error(), http.StatusBadRequest)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		if err := json.NewEncoder(w).Encode(game); err != nil {
			http.Error(w, "Failed to encode response", http.StatusInternalServerError)
			return
		}
	})

	http.HandleFunc("/game/finish", func(w http.ResponseWriter, r *http.Request) {
		// NOTE: When i want to test it locally uncomment this lines to enable
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")

		// Handle preflight requests
		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusOK)
			return
		}
		// NOTE: Here is the end of the uncommented lines

		if r.Method != http.MethodPost {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return
		}

		var req entities.RequestGameFinish
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			http.Error(w, "Invalid JSON: "+err.Error(), http.StatusBadRequest)
			return
		}
		defer r.Body.Close()

		stat, err := manager.FinishGame(req)
		if err != nil {
			http.Error(w, "Invalid Category or Tag: "+err.Error(), http.StatusBadRequest)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		if err := json.NewEncoder(w).Encode(stat); err != nil {
			http.Error(w, "Failed to encode response", http.StatusInternalServerError)
			return
		}
	})

	log.Println("Server running on http://localhost:8080")
	http.ListenAndServe(":8080", nil)
}
