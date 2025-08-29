# world-quiz-server

## Data Source Quiz

- **Countries, Continents, Oceans and Seas**: Ultimate Geography v5.3 ([Anki Deck](https://ankiweb.net/shared/info/2109889812), [Github Page](https://github.com/anki-geo/ultimate-geography/tree/master))


## Notes

Record.go, hat:
- id record
- id iduser
- id game
- id card
- ob loesen konnen, 
- zeit wann gelost
- category eg. mapname, flagname, capitalname, namecapital

Game
- id game
- id user
- cards solved
- games ist einfach für statistik, der algorithmus leitet das ganze aud den records ab

User
- was auch immer es braucht

## TODO Server

- [ ] Get Real id with UUID instead of int 1 for a game
- [ ] Save the game and remove it from the manager
- [ ] Write test

## Server

```shell
curl -X GET http://localhost:8080/hello
```

```shell
curl -X POST http://localhost:8080/echo \
     -H "Content-Type: application/json" \
     -d '{"text":"Hello Go"}'
```

```shell
curl -X POST http://localhost:8080/game \
  -H "Content-Type: application/json" \
  -d '{"category": 10, "tags": [0, 1]}'
```
