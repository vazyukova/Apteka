package com.example.apteki.controller;

import com.example.apteki.model.Usr;
import com.example.apteki.payload.AuthRequest;
import com.example.apteki.repository.UserRepository;
import com.example.apteki.service.UserService;
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
    public String auth (@RequestBody AuthRequest request) {
        if (userService.authUser(request.getUsername(), request.getPassword()))
            return "Пользователь " + request.getUsername() + " авторизован";
        else
            return "Неудачная попытка авторизации";
    }

    @PostMapping("/registration")
    public Usr registration(@RequestBody Usr user) {
        return userService.saveUser(user.getUsername(), user.getEmail(), user.getPassword());
    }
}
