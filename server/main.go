package main

import (
	"fmt"
	"log"
	"world-quiz/internal/entities"
	"world-quiz/internal/filter"
	"world-quiz/internal/reader"
)

func main() {
	places, err := reader.Read("data/")
	if err != nil {
		log.Fatal(err)
	}
	tags := []entities.Tag{}
	filtered := filter.Filter(&places, entities.MapName, tags)
	for _, p := range filtered {
		fmt.Println(p)
	}
}
