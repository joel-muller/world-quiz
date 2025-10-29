package entities

import "github.com/google/uuid"

type RequestGameStats struct {
	ID uuid.UUID `json:"id"`
}

type GameStats struct {
	Id   uuid.UUID `json:"id"`
	Info string    `json:"info"`
}
