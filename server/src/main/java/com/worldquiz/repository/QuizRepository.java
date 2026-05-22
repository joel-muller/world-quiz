/* (C)2026 */
package com.worldquiz.repository;

import com.worldquiz.entities.Quiz;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, UUID> {}
