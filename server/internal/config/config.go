package config

import (
	"github.com/joho/godotenv"
	"log"
	"os"
	"sync"
)

type Config struct {
	JWTSecret string
}

var (
	instance *Config
	once     sync.Once
)

func GetConfig() *Config {
	once.Do(func() {
		_ = godotenv.Load()

		jwt := lookUpEnv("JWT_SECRET")
		instance = &Config{
			JWTSecret: jwt,
		}
	})
	return instance
}

func lookUpEnv(varname string) string {
	value, exists := os.LookupEnv(varname)
	if !exists || value == "" {
		log.Fatalf("missing required environment variable: %s", varname)
	}
	return value
}
