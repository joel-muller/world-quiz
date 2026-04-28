/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.entities.User;
import com.worldquiz.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(String username, String email) {
        User user = new User(UUID.randomUUID(), username, email);

        return userRepository.save(user);
    }
}
