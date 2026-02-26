package com.querystore.server.controller;

import com.querystore.server.model.RegisterRequest;
import com.querystore.server.model.User;
import com.querystore.server.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest request) { // Reuse RegisterRequest as it has username and password
        Optional<User> user = authService.authenticate(request.getUsername(), request.getPassword());
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getUsername());
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
