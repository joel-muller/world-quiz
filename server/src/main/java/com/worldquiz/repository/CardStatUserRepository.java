/* (C)2026 */
package com.worldquiz.repository;

import com.worldquiz.entities.CardStatUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardStatUserRepository extends MongoRepository<CardStatUser, UUID> {
    List<CardStatUser> findAllByUserid(UUID userid);
}
