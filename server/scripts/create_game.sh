#!/bin/bash

curl -X POST http://localhost:8080/api/quiz \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $1" \
  -d '{
    "categories": [1, 2],
    "tags": [3, 4],
    "number": 10
}'
