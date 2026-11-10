package com.jobsfindai.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PasswordEncoderTest {

    @Test
    void shouldMatchCorrectPassword(){

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String hash = passwordEncoder.encode("MyPassword123");

        boolean valid = passwordEncoder.matches("MyPassword123", hash);

        assertTrue(valid);
    }

    @Test
    void shouldRejectIncorrectPassword() {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String hash = passwordEncoder.encode("MyPassword123");

        boolean valid = passwordEncoder.matches("WrongPassword", hash);

        assertFalse(valid);
    }

}
