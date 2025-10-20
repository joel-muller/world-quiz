#!/bin/bash

curl -X POST http://localhost:8080/game/finish \
  -H "Content-Type: application/json" \
  -d "{\"id\": \"$1\"}"
