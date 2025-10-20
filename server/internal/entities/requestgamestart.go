package entities

type RequestGameStart struct {
	Categories []Category `json:"categories"`
	Tags       []Tag      `json:"tags"`
	Number     *int       `json:"number,omitempty"`
}
