package com.jobsfindai.backend.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = new User();

        when(userRepository.findByEmail("alex@example.com"))
                .thenReturn(Optional.of(user));

        boolean result = userService.emailExists("alex@example.com");
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("alex@example.com"))
                .thenReturn(Optional.empty());

        boolean result = userService.emailExists("alex@example.com");

        assertFalse(result);
    }
}
