#!/bin/bash

curl -X POST http://localhost:8080/api/quiz/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@mail.com",
    "password": "Jonny"
}'
