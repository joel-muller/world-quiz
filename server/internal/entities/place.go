package entities

import "fmt"

type Place struct {
	Id          int
	Name        string
	PlaceInfo   string
	Capital     string
	CapitalInfo string
	RegionCode  string
	Maps        string
	Flag        string
	FlagInfo    string
	Tags        []Tag
}

func (p Place) String() string {
	return fmt.Sprintf(
		"ID: %v, Name: %s, PlaceInfo: %s, Capital: %s, CapitalInfo: %s, RegionCode: %s, Maps: %s, Flag: %s, FlagInfo: %s, Tags: %v",
		p.Id,
		valueOrEmpty(p.Name),
		valueOrEmpty(p.PlaceInfo),
		valueOrEmpty(p.Capital),
		valueOrEmpty(p.CapitalInfo),
		valueOrEmpty(p.RegionCode),
		valueOrEmpty(p.Maps),
		valueOrEmpty(p.Flag),
		valueOrEmpty(p.FlagInfo),
		p.Tags,
	)
}

func (p Place) GetCard(c Category) Card {
	switch c {
	case NameCapital:
		return Card{p.Id, c, p.Name, p.PlaceInfo, p.Capital, p.CapitalInfo}
	case CapitalName:
		return Card{p.Id, c, p.Capital, p.CapitalInfo, p.Name, p.PlaceInfo}
	case FlagName:
		return Card{p.Id, c, p.Flag, "", p.nameWithCapital(), p.backFlagInfo()}
	case MapName:
		return Card{p.Id, c, p.Maps, "", p.nameWithCapital(), p.backMapInfo()}
	default:
		return Card{}
	}
}

func (p Place) nameWithCapital() string {
	if p.Capital != "" {
		return fmt.Sprintf("%s (%s)", p.Name, p.Capital)
	}
	return p.Name
}

func (p Place) backMapInfo() string {
	return fmt.Sprintf("%s %s", p.PlaceInfo, p.CapitalInfo)
}

func (p Place) backFlagInfo() string {
	return fmt.Sprintf("%s %s %s", p.PlaceInfo, p.CapitalInfo, p.FlagInfo)
}

func valueOrEmpty(s string) string {
	if s == "" {
		return "empty"
	}
	return s
}
