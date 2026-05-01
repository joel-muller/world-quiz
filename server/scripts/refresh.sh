#!/bin/bash

curl -X POST http://localhost:8080/api/quiz/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$1\"}"
