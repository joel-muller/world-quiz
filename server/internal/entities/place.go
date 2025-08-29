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
	Tags        []Tag
}

func (p Place) String() string {
	return fmt.Sprintf(
		"ID: %v, Name: %s, PlaceInfo: %s, Capital: %s, CapitalInfo: %s, RegionCode: %s, Maps: %s, Flag: %s, Tags: %v",
		p.Id,
		valueOrEmpty(p.Name),
		valueOrEmpty(p.PlaceInfo),
		valueOrEmpty(p.Capital),
		valueOrEmpty(p.CapitalInfo),
		valueOrEmpty(p.RegionCode),
		valueOrEmpty(p.Maps),
		valueOrEmpty(p.Flag),
		p.Tags,
	)
}

func (p Place) GetCard(c Category) Card {
	switch c {
	case NameCapital:
		return Card{c, p.Name, p.PlaceInfo, p.Capital, p.CapitalInfo}
	case CapitalName:
		return Card{c, p.Capital, p.CapitalInfo, p.Name, p.PlaceInfo}
	case FlagName:
		return Card{c, p.Flag, "", p.nameWithCapital(), p.bothInfos()}
	case MapName:
		return Card{c, p.Maps, "", p.nameWithCapital(), p.bothInfos()}
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

func (p Place) bothInfos() string {
	return fmt.Sprintf("%s %s", p.Name, p.Capital)
}

func valueOrEmpty(s string) string {
	if s == "" {
		return "empty"
	}
	return s
}
