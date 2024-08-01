package com.ecommerce.controller;

import com.ecommerce.exception.UserException;
import com.ecommerce.modal.User;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserProfileHandler_Success() throws UserException {
        String jwt = "jwtToken";
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserProfileHandler(jwt);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserProfileHandler_UserException() throws UserException {
        String jwt = "jwtToken";

        when(userService.findUserProfileByJwt(jwt)).thenThrow(new UserException("User not found"));

        UserException thrown = assertThrows(UserException.class, () -> {
            userController.getUserProfileHandler(jwt);
        });

        assertEquals("User not found", thrown.getMessage());
    }
}
