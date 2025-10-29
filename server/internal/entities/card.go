package entities

type Card struct {
	PlaceId   int      `json:"placeId"`
	Category  Category `json:"category"`
	Front     string   `json:"front"`
	InfoFront string   `json:"frontInfo"`
	Back      string   `json:"back"`
	InfoBack  string   `json:"backInfo"`
}
