package entities

import "github.com/google/uuid"

type RequestGameFinish struct {
	ID uuid.UUID `json:"id"`
}
