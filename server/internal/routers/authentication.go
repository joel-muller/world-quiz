package internal

import (
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
	"time"
	"world-quiz/internal/config"
	"world-quiz/internal/entities"
)

// NOTE: Username and password are still stored in map, will get replaced by databaes
var users = map[string]string{}

func protectedHandler(req entities.RequestProtected, username string) (entities.ResponseProtected, error) {
	reply := fmt.Sprintf("Hello %s, your message was: %s", username, req.Message)
	return entities.ResponseProtected{Reply: reply}, nil
}

func registerHandler(req entities.RequestRegister) (entities.ResponseRegister, error) {
	if _, exists := users[req.Username]; exists {
		return entities.ResponseRegister{}, fmt.Errorf("username already exists")
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return entities.ResponseRegister{}, fmt.Errorf("failed to hash password")
	}

	users[req.Username] = string(hashedPassword)
	return entities.ResponseRegister{Message: "User registered successfully"}, nil
}

func loginHandler(req entities.RequestLogin) (entities.ResponseLogin, error) {
	storedPassword, exists := users[req.Username]
	if !exists {
		return entities.ResponseLogin{}, fmt.Errorf("invalid username or password")
	}

	if err := bcrypt.CompareHashAndPassword([]byte(storedPassword), []byte(req.Password)); err != nil {
		return entities.ResponseLogin{}, fmt.Errorf("invalid username or password")
	}

	expirationTime := time.Now().Add(24 * time.Hour)
	claims := &entities.Claims{
		Username: req.Username,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}

	config := config.GetConfig()

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString([]byte(config.JWTSecret))
	if err != nil {
		return entities.ResponseLogin{}, fmt.Errorf("failed to generate token: %v", err)
	}

	return entities.ResponseLogin{Token: tokenString}, nil
}

func AuthenticationRouter() {
	handlePost("/register", registerHandler)
	handlePost("/login", loginHandler)
	handleProtectedPost("/protected", protectedHandler)
}
