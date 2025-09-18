#!/bin/bash

curl -X POST http://localhost:8080/game \
  -H "Content-Type: application/json" \
  -d '{"categories": [0, 1], "tags": [0, 1], "number": 10}'
