package com.aniruddha_first;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aniruddha_second.UserRepository;

@RestController
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/api/users/signup")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody User user) throws ResourceNotFoundException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceNotFoundException();
        }

        
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/api/users/signin")
    public ResponseEntity<Map<String, String>> authenticateUser(@Valid @RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Map<String, String> response = new HashMap<>();
        response.put("jwt", jwt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/users/resetpassword")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody Map<String, String> request) throws ResourceNotFoundException {
        String email = request.get("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException();
        }

        
        String token = UUID.randomUUID().toString();
        User user = optionalUser.get();
        user.setResetToken(token);
        userRepository.save(user);

        
        String resetUrl = "http://localhost:8080/api/users/changepassword?email=" + email + "&token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Password reset request");
        mailMessage.setText("Please click the following link to reset your password: " + resetUrl);
        mailSender.send(mailMessage);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset link sent to your email address");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/users/changepassword")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody Map<String, String> request) throws InvalidRequestException, ResourceNotFoundException {
        String email = request.get("email");
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException();
        }

        User user = optionalUser.get();
        if (!user.getResetToken().equals(token)) {
            throw new InvalidRequestException();
        }

        
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        user.setResetToken(null);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
