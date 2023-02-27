package com.aniruddha_third;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aniruddha_first.User;
import com.aniruddha_second.UserRepository;

@Service
public class UserService {
	
	@Autowired
	   private UserRepository userRepository;
	   @Autowired
	   private PasswordEncoder passwordEncoder;
	   @Autowired
	   private JavaMailSender javaMailSender;

	   public void register(User user) {
	      String encodedPassword = passwordEncoder.encode(user.getPassword());
	      user.setPassword(encodedPassword);
	      userRepository.save(user);
	   }

	   public UserDetails authenticate(String email, String password) throws UsernameNotFoundException, BadCredentialsException {
	      User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	      if (!passwordEncoder.matches(password, user.getPassword())) {
	         throw new BadCredentialsException("Invalid username/password");
	      }
	      return new UserPrincipal(user);
	   }

	   public void resetPassword(String email) throws MessagingException {
	      User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	      String password = generatePassword();
	      user.setPassword(passwordEncoder.encode(password));
	      userRepository.save(user);
	      sendPasswordResetEmail(user.getEmail(), password);
	   }

	   private String generatePassword() {
		   return "newPassword";
	   }

	   private void sendPasswordResetEmail(String email, String password) throws MessagingException {
	      MimeMessage message = javaMailSender.createMimeMessage();
	      MimeMessageHelper helper = new MimeMessageHelper(message, true);
	      helper.setTo(email);
	      helper.setSubject("Password reset request");
	      helper.setText("Your new password is: " + password);
	      javaMailSender.send(message);
	   }

	public UserDetails loadUserByUsername(String email) {
		// TODO Auto-generated method stub
		return null;
	}
	}


