package com.example.apteki.payload;

import com.example.apteki.model.Usr;

public class AuthResponse {
    private Usr user;
    private String message;

    public AuthResponse(Usr user, String message) {
        this.user = user;
        this.message = message;
    }

    public AuthResponse(){

    }

    public Usr getUser() {
        return user;
    }

    public void setUser(Usr user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
