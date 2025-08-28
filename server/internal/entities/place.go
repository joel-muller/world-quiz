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

func valueOrEmpty(s string) string {
	if s == "" {
		return "empty"
	}
	return s
}
