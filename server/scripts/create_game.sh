#!/bin/bash

curl -X POST http://localhost:8080/quiz \
  -H "Content-Type: application/json" \
  -d '{
    "category": "CAPITAL_NAME",
    "tags": ["EUROPE", "NORTH_AMERICA"],
    "number": 10
  }'
