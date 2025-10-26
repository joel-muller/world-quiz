#!/bin/bash

curl -X POST http://localhost:8080/protected \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $1" \
  -d '{
    "message": "Hello from curl"
  }'
