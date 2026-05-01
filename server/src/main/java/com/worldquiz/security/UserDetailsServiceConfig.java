/* (C)2026 */
package com.worldquiz.security;

import com.worldquiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username ->
                userRepository.findByUsername(username).stream()
                        .findFirst()
                        .map(
                                u ->
                                        User.builder()
                                                .username(u.username())
                                                .password(u.password())
                                                .roles("USER")
                                                .build())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
