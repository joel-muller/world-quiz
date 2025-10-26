package internal

import (
	"encoding/json"
	"errors"
	"github.com/golang-jwt/jwt/v5"
	"net/http"
	"strings"
	"world-quiz/internal/config"
)

func setCORSHeaders(w http.ResponseWriter) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
}

func validatePostMethod(w http.ResponseWriter, r *http.Request) bool {
	if r.Method == http.MethodOptions {
		w.WriteHeader(http.StatusOK)
		return false
	}
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return false
	}
	return true
}

func decodeJSONRequest[T any](w http.ResponseWriter, r *http.Request) (*T, bool) {
	var req T
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid JSON: "+err.Error(), http.StatusBadRequest)
		return nil, false
	}
	defer r.Body.Close()
	return &req, true
}

func writeJSONResponse(w http.ResponseWriter, res any) {
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(res); err != nil {
		http.Error(w, "Failed to encode response", http.StatusInternalServerError)
	}
}

func extractUsernameFromJWT(r *http.Request) (string, error) {
	authHeader := r.Header.Get("Authorization")
	if !strings.HasPrefix(authHeader, "Bearer ") {
		return "", errors.New("missing or invalid Authorization header")
	}

	tokenString := strings.TrimPrefix(authHeader, "Bearer ")
	cfg := config.GetConfig()
	token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (any, error) {
		return []byte(cfg.JWTSecret), nil
	})
	if err != nil || !token.Valid {
		return "", errors.New("invalid or expired token")
	}

	claims, ok := token.Claims.(*Claims)
	if !ok {
		return "", errors.New("invalid token claims")
	}

	return claims.Username, nil
}

func makeHandler[Request any, Response any](
	handler func(req Request, username *string) (Response, error),
	requireAuth bool,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		setCORSHeaders(w)
		if !validatePostMethod(w, r) {
			return
		}

		var username *string
		if requireAuth {
			u, err := extractUsernameFromJWT(r)
			if err != nil {
				http.Error(w, err.Error(), http.StatusUnauthorized)
				return
			}
			username = &u
		}

		req, ok := decodeJSONRequest[Request](w, r)
		if !ok {
			return
		}

		res, err := handler(*req, username)
		if err != nil {
			http.Error(w, "Bad Request: "+err.Error(), http.StatusBadRequest)
			return
		}

		writeJSONResponse(w, res)
	}
}

func handlePost[Request any, Response any](path string, handler func(req Request) (Response, error)) {
	http.HandleFunc(path, makeHandler(func(req Request, _ *string) (Response, error) {
		return handler(req)
	}, false))
}

func handleProtectedPost[Request any, Response any](path string, handler func(req Request, username string) (Response, error)) {
	http.HandleFunc(path, makeHandler(func(req Request, username *string) (Response, error) {
		return handler(req, *username)
	}, true))
}
