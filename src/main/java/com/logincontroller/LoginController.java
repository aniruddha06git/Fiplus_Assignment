package com.logincontroller;

import javax.naming.AuthenticationException;
import com.aniruddha_first.LoginRequest;
import com.aniruddha_first.JwtResponse;
import com.aniruddha_first.ErrorResponse;
import com.aniruddha_second.JwtTokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aniruddha_third.UserService;

@RestController
public class LoginController {
	 @Autowired
	   private UserService userService;

	   @Autowired
	   private AuthenticationManager authenticationManager;

	   @Autowired
	   private JwtTokenUtil jwtTokenUtil;

	   @PostMapping("/login")
	   public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
	      authenticationManager.authenticate(
		       new UsernamePasswordAuthenticationToken(
		             loginRequest.getEmail(), loginRequest.getPassword()));

		 final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
		 final String token = jwtTokenUtil.generateToken(userDetails);

		 return ResponseEntity.ok(new JwtResponse());
	   }
	}
	
	


