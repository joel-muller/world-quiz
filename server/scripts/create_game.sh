#!/bin/bash

curl -X POST http://localhost:8080/quiz \
  -H "Content-Type: application/json" \
  -d '{
    "categories": [1, 2],
    "tags": [3, 4],
    "number": 10
  }'
