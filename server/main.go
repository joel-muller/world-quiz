package main

import (
	"fmt"
	"log"
	"world-quiz/internal/reader"
)

func main() {
	places, err := reader.Read("data/")
	if err != nil {
		log.Fatal(err)
	}
	for _, p := range places {
		fmt.Println(p)
	}
}
