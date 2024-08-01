package com.ecommerce.controller;

import com.ecommerce.exception.ProductException;
import com.ecommerce.modal.Product;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private UserProductController userProductController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindProductByCategoryHandler_Success() {
        Page<Product> productsPage = new PageImpl<>(Collections.singletonList(new Product()));
        when(productService.getAllProduct(
                anyString(), anyList(), anyList(), anyInt(), anyInt(), anyInt(), anyString(),
                anyString(), anyInt(), anyInt(), anyString(), anyInt()))
                .thenReturn(productsPage);

        ResponseEntity<Page<Product>> response = userProductController.findProductByCategoryHandler(
                "category", Collections.singletonList("color"), Collections.singletonList("size"),
                0, 1000, 10, "asc", "in_stock", 1, 10, "brand", 4
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productsPage, response.getBody());
    }

    @Test
    void testFindProductByIdHandler_Success() throws ProductException {
        Long productId = 1L;
        Product product = new Product();
        when(productService.findProductById(productId)).thenReturn(product);

        ResponseEntity<Product> response = userProductController.findProductByIdHandler(productId);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product, response.getBody());
    }

    @Test
    void testFindProductByIdHandler_ProductException() throws ProductException {
        Long productId = 1L;
        when(productService.findProductById(productId)).thenThrow(new ProductException("Product not found"));

        ProductException thrown = assertThrows(ProductException.class, () -> {
            userProductController.findProductByIdHandler(productId);
        });

        assertEquals("Product not found", thrown.getMessage());
    }

    @Test
    void testSearchProductHandler_Success() {
        List<Product> products = Collections.singletonList(new Product());
        when(productService.searchProduct(anyString())).thenReturn(products);

        ResponseEntity<List<Product>> response = userProductController.searchProductHandler("query");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(products, response.getBody());
    }
}
