package com.example.testAI.testAIRagVectorDB.code.Security;

import com.example.testAI.testAIRagVectorDB.code.Entity.User;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.InvalidRequestException;
import com.example.testAI.testAIRagVectorDB.code.repo.UserRepository;
import com.example.testAI.testAIRagVectorDB.code.Security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidRequestException("Invalid username or password");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    public String registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new InvalidRequestException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);
        return "User registered: " + username;
    }

    public String registerAdmin(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new InvalidRequestException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");
        userRepository.save(user);
        return "Admin registered: " + username;
    }
}
