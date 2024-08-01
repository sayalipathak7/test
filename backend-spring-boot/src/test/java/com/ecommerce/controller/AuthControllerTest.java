package com.ecommerce.controller;

import com.ecommerce.modal.Address;
import com.ecommerce.modal.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.request.LoginRequest;
import com.ecommerce.response.AuthResponse;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CustomUserDetails;
import com.ecommerce.config.JwtTokenProvider;
import com.ecommerce.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private CartService cartService;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserHandler_Success() throws UserException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setCity("City");
        user.setState("State");
        user.setZip("12345");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(addressRepository.save(any(Address.class))).thenReturn(new Address());
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        ResponseEntity<AuthResponse> response = authController.createUserHandler(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwtToken", response.getBody().getJwt());
        //assertTrue(response.getBody().isStatus());
    }

    @Test
    void testCreateUserHandler_EmailAlreadyExists() throws UserException {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(new User());

        UserException thrown = assertThrows(UserException.class, () -> {
            authController.createUserHandler(user);
        });

        assertEquals("Email Is Already Used With Another Account", thrown.getMessage());
    }

    @Test
    void testSignin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetails.loadUserByUsername(loginRequest.getEmail())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        ResponseEntity<AuthResponse> response = authController.signin(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwtToken", response.getBody().getJwt());
        //assertTrue(response.getBody().isStatus());
    }

    @Test
    void testSignin_BadCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(customUserDetails.loadUserByUsername(loginRequest.getEmail())).thenReturn(null);

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> {
            authController.signin(loginRequest);
        });

        assertEquals("Invalid username or password", thrown.getMessage());
    }

    @Test
    void testSignin_PasswordMismatch() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetails.loadUserByUsername(loginRequest.getEmail())).thenReturn(userDetails);
        when(passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())).thenReturn(false);

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> {
            authController.signin(loginRequest);
        });

        assertEquals("Invalid username or password", thrown.getMessage());
    }
}
