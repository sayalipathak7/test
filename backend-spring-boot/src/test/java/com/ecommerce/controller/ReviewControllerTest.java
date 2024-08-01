package com.ecommerce.controller;

import com.ecommerce.exception.ProductException;
import com.ecommerce.exception.UserException;
import com.ecommerce.modal.Review;
import com.ecommerce.modal.User;
import com.ecommerce.request.ReviewRequest;
import com.ecommerce.service.ReviewService;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReviewHandler_Success() throws UserException, ProductException {
        String jwt = "jwtToken";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setReview("Great product!");

        User user = new User();
        Review review = new Review();

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);
        when(reviewService.createReview(reviewRequest, user)).thenReturn(review);

        ResponseEntity<Review> response = reviewController.createReviewHandler(reviewRequest, jwt);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(review, response.getBody());
    }

    @Test
    void testCreateReviewHandler_UserException() throws UserException, ProductException {
        String jwt = "jwtToken";
        ReviewRequest reviewRequest = new ReviewRequest();

        when(userService.findUserProfileByJwt(jwt)).thenThrow(new UserException("User not found"));

        UserException thrown = assertThrows(UserException.class, () -> {
            reviewController.createReviewHandler(reviewRequest, jwt);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testCreateReviewHandler_ProductException() throws UserException, ProductException {
        String jwt = "jwtToken";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setReview("Great product!");

        User user = new User();

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);
        when(reviewService.createReview(reviewRequest, user)).thenThrow(new ProductException("Product not found"));

        ProductException thrown = assertThrows(ProductException.class, () -> {
            reviewController.createReviewHandler(reviewRequest, jwt);
        });

        assertEquals("Product not found", thrown.getMessage());
    }

    @Test
    void testGetProductsReviewHandler_Success() {
        Long productId = 1L;
        List<Review> reviews = Collections.singletonList(new Review());

        when(reviewService.getAllReview(productId)).thenReturn(reviews);

        ResponseEntity<List<Review>> response = reviewController.getProductsReviewHandler(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reviews, response.getBody());
    }
}
