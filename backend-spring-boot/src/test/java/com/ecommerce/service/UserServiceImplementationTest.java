package com.ecommerce.service;

import com.ecommerce.config.JwtTokenProvider;
import com.ecommerce.exception.UserException;
import com.ecommerce.modal.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImplementation userService;

    private User user;
    private String jwtToken;
    private String email;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize objects for testing
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        jwtToken = "dummy-jwt-token";
        email = "user@example.com";
    }

    @Test
    void testFindUserById_Success() throws UserException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUserById_Failure() throws UserException {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserException thrown = assertThrows(UserException.class, () -> {
            userService.findUserById(1L);
        });

        assertEquals("user not found with id 1", thrown.getMessage());
    }

    @Test
    void testFindUserProfileByJwt_Success() throws UserException {
        when(jwtTokenProvider.getEmailFromJwtToken(jwtToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.findUserProfileByJwt(jwtToken);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(jwtTokenProvider, times(1)).getEmailFromJwtToken(jwtToken);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindUserProfileByJwt_Failure() throws UserException {
        when(jwtTokenProvider.getEmailFromJwtToken(jwtToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        UserException thrown = assertThrows(UserException.class, () -> {
            userService.findUserProfileByJwt(jwtToken);
        });

        assertEquals("user not exist with email " + email, thrown.getMessage());
    }
}
