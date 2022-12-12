package com.example.apteki.controller;

import com.example.apteki.model.Usr;
import com.example.apteki.payload.AuthRequest;
import com.example.apteki.payload.AuthResponse;
import com.example.apteki.repository.UserRepository;
import com.example.apteki.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> auth (@RequestBody AuthRequest request) {
        if (userService.authUser(request.getUsername(), request.getPassword()))
            return new ResponseEntity<>(new AuthResponse(userRepository.findByUsername(request.getUsername()).get(),"Пользователь " + request.getUsername() + " авторизован"), HttpStatus.OK);
        else
            return new ResponseEntity<>(new AuthResponse(new Usr("-", "-", "-"), "Неудачная попытка авторизации"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponse> registration(@RequestBody Usr user) {
        if (userRepository.existsByUsername(user.getUsername()))
            return ResponseEntity.badRequest().body(new AuthResponse(new Usr("-", "-", "-"), "Пользователь с таким username существует"));
        else if (userRepository.existsByEmail(user.getEmail()))
            return ResponseEntity.badRequest().body(new AuthResponse(new Usr("-", "-", "-"), "Пользователь с таким email существует"));
        return new ResponseEntity<>(new AuthResponse(userService.saveUser(user.getUsername(), user.getEmail(), user.getPassword()), "Пользователь создан успешно"), HttpStatus.OK);
    }
}
