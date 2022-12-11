package com.example.apteki.service;

import com.example.apteki.model.Usr;
import com.example.apteki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, username)));
    }

    public Usr saveUser(String username, String email, String password) {
        return userRepository.save(new Usr(username, email, (new BCryptPasswordEncoder()).encode(password)));
    }

    public boolean authUser(String username, String password) {
        Usr user;
        user = userRepository.findByUsername(username)
                .orElse(new Usr("", "", ""));
        return Objects.equals(user.getUsername(), username) && (new BCryptPasswordEncoder()).matches(password, user.getPassword());
    }
}
