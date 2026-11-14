package com.jobsfindai.backend.auth;

import com.jobsfindai.backend.security.JwtService;
import com.jobsfindai.backend.user.User;
import com.jobsfindai.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest(
                "alex@example.com",
                "Password123"
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Password123"))
                .thenReturn("hashed-password");

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("alex@example.com", savedUser.getEmail());
        assertEquals("hashed-password", savedUser.getPasswordHash());
    }

    @Test
    void shouldRejectDuplicateEmail() {

        RegisterRequest request = new RegisterRequest(
                "alex@example.com",
                "Password123"
        );

        User existingUser = new User(
                "alex@example.com",
                "existing-hash"
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(existingUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }


    @Test
    void shouldLoginWithCorrectPassword(){


        LoginRequest request = new LoginRequest(
                "alex@example.com",
                "Password123"
        );

        User user = new User(
                "alex@example.com",
                "hashed-password"
        );

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "hashed-password"))
                .thenReturn(true);

        String token = authService.login(request);

        assertEquals("test-jwt-token", token);
    }


    @Test
    void shouldRejectIncorrectPassword() {

        LoginRequest request = new LoginRequest(
                "alex@example.com",
                "Password123"
        );

        User user = new User(
                "alex@example.com",
                "hashed-password"
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));


        when(passwordEncoder.matches("Password123", "hashed-password"))
                .thenReturn(false);


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void shouldRejectUnknownEmail() {

        LoginRequest request = new LoginRequest(
                "alex@example.com",
                "Password123"
        );


        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(passwordEncoder, never()).matches(anyString(), anyString());

        verify(jwtService, never()).generateToken(any(User.class));
    }

}
