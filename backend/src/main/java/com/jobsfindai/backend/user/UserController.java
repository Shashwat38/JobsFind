package com.jobsfindai.backend.user;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public Long getCurrentUserId(@AuthenticationPrincipal Jwt jwt) {

        String subject = Objects.requireNonNull(jwt.getSubject());

        return Long.parseLong(subject);

    }
}
