package entities

import "github.com/google/uuid"

type RequestGame struct {
	Categories []Category `json:"categories"`
	Tags       []Tag      `json:"tags"`
	Number     *int       `json:"number,omitempty"`
}

type Game struct {
	Id    uuid.UUID `json:"id"`
	Cards []Card    `json:"cards"`
}
