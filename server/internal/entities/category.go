package entities

type Category int

const (
	MapName Category = iota
	FlagName
	CapitalName
	NameCapital
)

func (t Category) Valid() bool {
	return t >= MapName && t <= NameCapital
}
