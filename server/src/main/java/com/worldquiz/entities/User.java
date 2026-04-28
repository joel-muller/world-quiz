/* (C)2026 */
package com.worldquiz.entities;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public record User(@Id UUID id, String username, String email) {}
