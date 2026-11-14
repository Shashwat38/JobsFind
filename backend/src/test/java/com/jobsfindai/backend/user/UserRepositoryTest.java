package com.jobsfindai.backend.user;

import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {

        User user = new User("repository-test@example.com", "test-hash");
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("repository-test@example.com");

        assertTrue(result.isPresent());

        assertEquals("repository-test@example.com", result.get().getEmail());

    }
}
