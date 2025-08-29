package game

import "world-quiz/internal/entities"

type Game struct {
	Category entities.Category
	Places   []entities.Place
}

func (g Game) Active() bool {
	return len(g.Places) > 0
}

func (g Game) CurrentCard() entities.Card {
	if g.Active() {
		return g.Places[0].GetCard(g.Category)
	}
	return entities.Place{}.GetCard(g.Category)
}

func (g *Game) Guess(guess bool) {
	if guess {
		g.removeFirst()
	} else {
		g.removeFirstAppendLast()
	}
}

func (g *Game) removeFirst() {
	if len(g.Places) > 0 {
		g.Places = g.Places[1:]
	}
}

func (g *Game) removeFirstAppendLast() {
	if len(g.Places) > 0 {
		first := g.Places[0]
		g.Places = append(g.Places[1:], first)
	}
}
