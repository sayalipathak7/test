package com.ecommerce.service;

import com.ecommerce.modal.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetails customUserDetails;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the User object for testing
        user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);

        UserDetails userDetails = customUserDetails.loadUserByUsername("testuser@example.com");

        assertNotNull(userDetails);
        assertEquals("testuser@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findByEmail("testuser@example.com");
    }

    @Test
    void testLoadUserByUsername_UsernameNotFoundException() {
        when(userRepository.findByEmail("nonexistentuser@example.com")).thenReturn(null);

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetails.loadUserByUsername("nonexistentuser@example.com");
        });

        assertEquals("user not found with email nonexistentuser@example.com", thrown.getMessage());

        verify(userRepository, times(1)).findByEmail("nonexistentuser@example.com");
    }
}
