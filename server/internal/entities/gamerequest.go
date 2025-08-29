package entities

type GameRequest struct {
	Category Category `json:"category"`
	Tags     []Tag    `json:"tags"`
}
