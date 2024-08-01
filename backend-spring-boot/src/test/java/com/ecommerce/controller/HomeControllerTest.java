package com.ecommerce.controller;

import com.ecommerce.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    private HomeController homeController;

    @BeforeEach
    void setUp() {
        homeController = new HomeController();
    }

    @Test
    void testHomeController() {
        // Act
        ResponseEntity<ApiResponse> response = homeController.homeController();

        // Assert
        assertNotNull(response);
        //Verifies that the HTTP status code of the response is 200 OK.
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Welcome To E-Commerce System", apiResponse.getMessage());

    }
}