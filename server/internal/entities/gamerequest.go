package entities

type GameRequest struct {
	Categories []Category `json:"categories"`
	Tags       []Tag      `json:"tags"`
	Number     int        `json:"number"`
}
