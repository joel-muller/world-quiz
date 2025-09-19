package entities

import "github.com/google/uuid"

type Game struct {
	Id    uuid.UUID
	Cards []Card
}
