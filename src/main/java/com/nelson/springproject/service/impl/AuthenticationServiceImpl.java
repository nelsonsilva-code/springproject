package com.nelson.springproject.service.impl;

import com.nelson.springproject.dto.*;
import com.nelson.springproject.entity.User;
import com.nelson.springproject.exception.InvalidPasswordException;
import com.nelson.springproject.exception.UserAlreadyExistsException;
import com.nelson.springproject.exception.UserNotFoundException;
import com.nelson.springproject.repository.UserRepository;
import com.nelson.springproject.security.JwtTokenProvider;
import com.nelson.springproject.service.AuthenticationService;
import com.nelson.springproject.utils.RandomPasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RandomPasswordGenerator randomPasswordGenerator;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     AuthenticationManager authenticationManager,
                                     JwtTokenProvider jwtTokenProvider, RandomPasswordGenerator randomPasswordGenerator) {
        this.userRepository        = userRepository;
        this.passwordEncoder       = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider      = jwtTokenProvider;
        this.randomPasswordGenerator = randomPasswordGenerator;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {

        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException());

        boolean passwordCorrect = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());

        if (!passwordCorrect) {
            throw new InvalidPasswordException();
        }

        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));


        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setUserId(user.getId());
        jwtAuthResponse.setAccessToken(token);
        return jwtAuthResponse;
    }

    public String createUser(String username) {
        Optional<User> userExists = userRepository.findByUsername(username);
        if (userExists.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = new User();

        user.setUsername(username);
        String randomPassword = randomPasswordGenerator.generateRandomPassword(18);
        logger.info("Created "+ user.getUsername() + " with password - " + randomPassword); // -> Logging the password because there is currently no way of sending it to the user (e.g. email service)
        user.setPassword(passwordEncoder.encode(randomPassword));

        userRepository.save(user);
        return randomPassword;
    };
}
