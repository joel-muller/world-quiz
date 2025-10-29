package entities

import "github.com/golang-jwt/jwt/v5"

type User struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type Claims struct {
	Username string `json:"username"`
	jwt.RegisteredClaims
}

type RequestRegister struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type ResponseRegister struct {
	Message string `json:"message"`
}

type RequestLogin struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type ResponseLogin struct {
	Token string `json:"token"`
}

type RequestProtected struct {
	Message string `json:"message"`
}

type ResponseProtected struct {
	Reply string `json:"reply"`
}
