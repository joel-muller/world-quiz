package internal

import (
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
	"time"
	"world-quiz/internal/config"
)

// NOTE: Username and password are still stored in map, will get replaced by databaes
var users = map[string]string{}

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

func protectedHandler(req RequestProtected, username string) (ResponseProtected, error) {
	reply := fmt.Sprintf("Hello %s, your message was: %s", username, req.Message)
	return ResponseProtected{Reply: reply}, nil
}

func registerHandler(req RequestRegister) (ResponseRegister, error) {
	if _, exists := users[req.Username]; exists {
		return ResponseRegister{}, fmt.Errorf("username already exists")
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return ResponseRegister{}, fmt.Errorf("failed to hash password")
	}

	users[req.Username] = string(hashedPassword)
	return ResponseRegister{Message: "User registered successfully"}, nil
}

func loginHandler(req RequestLogin) (ResponseLogin, error) {
	storedPassword, exists := users[req.Username]
	if !exists {
		return ResponseLogin{}, fmt.Errorf("invalid username or password")
	}

	if err := bcrypt.CompareHashAndPassword([]byte(storedPassword), []byte(req.Password)); err != nil {
		return ResponseLogin{}, fmt.Errorf("invalid username or password")
	}

	expirationTime := time.Now().Add(24 * time.Hour)
	claims := &Claims{
		Username: req.Username,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expirationTime),
		},
	}

	config := config.GetConfig()

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString([]byte(config.JWTSecret))
	if err != nil {
		return ResponseLogin{}, fmt.Errorf("failed to generate token: %v", err)
	}

	return ResponseLogin{Token: tokenString}, nil
}

func AuthenticationRouter() {
	handlePost("/register", registerHandler)
	handlePost("/login", loginHandler)
	handleProtectedPost("/protected", protectedHandler)
}
