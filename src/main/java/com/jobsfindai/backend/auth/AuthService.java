package com.jobsfindai.backend.auth;

import com.jobsfindai.backend.security.JwtService;
import com.jobsfindai.backend.user.User;
import com.jobsfindai.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        String hash = passwordEncoder.encode(request.password());

        User user = new User(request.email(), hash);

        userRepository.save(user);



    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        )
        );

       boolean passMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

       if (!passMatches) {
           throw new ResponseStatusException(
                   HttpStatus.UNAUTHORIZED,
                   "Invalid email or password"
           );
       }

        return jwtService.generateToken(user);
    }
}