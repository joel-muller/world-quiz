#!/bin/bash

curl -X POST http://localhost:8080/game \
  -H "Content-Type: application/json" \
  -d '{"category": [0, 1], "tags": [0, 1]}'
