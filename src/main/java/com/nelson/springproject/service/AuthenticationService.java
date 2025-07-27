package com.nelson.springproject.service;

import com.nelson.springproject.dto.*;

public interface AuthenticationService {
    JwtAuthResponse login(LoginDto loginDto);

    String createUser(String username);
}
