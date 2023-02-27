package com.aniruddha_controller;

import javax.mail.MessagingException;
import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aniruddha_first.ErrorResponse;
import com.aniruddha_first.JwtResponse;
import com.aniruddha_first.LoginRequest;
import com.aniruddha_first.User;
import com.aniruddha_second.JwtTokenUtil;
import com.aniruddha_third.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

   @Autowired
   private UserService userService;

   @Autowired
   private AuthenticationManager authenticationManager;

   @Autowired
   private JwtTokenUtil jwtTokenUtil;

   @PostMapping("/register")
   public ResponseEntity<?> register(@RequestBody UserDto userDto) {
      User user = new User();
      user.setEmail(userDto.getEmail());
      user.setPassword(((UserDto) userDto).getPassword());

      userService.register(user);
      return ResponseEntity.ok(user);
   }

   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
      authenticationManager.authenticate(
	       new UsernamePasswordAuthenticationToken(
	             loginRequest.getEmail(), loginRequest.getPassword()));

	 final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
	 final String token = jwtTokenUtil.generateToken(userDetails);

	 return ResponseEntity.ok(new JwtResponse());
   }

   @PostMapping("/resetPassword")
   public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest resetRequest) throws MessagingException {
      userService.resetPassword(resetRequest.getEmail());
      return ResponseEntity.ok(new Object());
   }
}
