package com.ecommerce.service;

import com.ecommerce.exception.ProductException;
import com.ecommerce.modal.Product;
import com.ecommerce.modal.Review;
import com.ecommerce.modal.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.request.ReviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceImplementationTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewServiceImplementation reviewService;

    private User user;
    private Product product;
    private ReviewRequest reviewRequest;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize objects for testing
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(1L);

        reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setReview("Excellent product!");

        review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview("Excellent product!");
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateReview_Success() throws ProductException {
        when(productService.findProductById(1L)).thenReturn(product);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Review result = reviewService.createReview(reviewRequest, user);

        assertNotNull(result);
        assertEquals(review.getReview(), result.getReview());
        verify(productService, times(1)).findProductById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateReview_ProductException() throws ProductException {
        when(productService.findProductById(1L)).thenThrow(new ProductException("Product not found"));

        ProductException thrown = assertThrows(ProductException.class, () -> {
            reviewService.createReview(reviewRequest, user);
        });

        assertEquals("Product not found", thrown.getMessage());
        verify(productService, times(1)).findProductById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetAllReview_Success() {
        when(reviewRepository.getAllProductsReview(1L)).thenReturn(Collections.singletonList(review));

        List<Review> result = reviewService.getAllReview(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(review.getReview(), result.get(0).getReview());
        verify(reviewRepository, times(1)).getAllProductsReview(1L);
    }
}
