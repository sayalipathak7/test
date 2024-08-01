package com.ecommerce.service;

import com.ecommerce.exception.ProductException;
import com.ecommerce.modal.Category;
import com.ecommerce.modal.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.request.CreateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceImplementationTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductServiceImplementation productService;

    private Product product;
    private CreateProductRequest createProductRequest;
    private Category topLevelCategory;
    private Category secondLevelCategory;
    private Category thirdLevelCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize objects for testing
        topLevelCategory = new Category();
        topLevelCategory.setName("Fruits");
        topLevelCategory.setLevel(1);

        secondLevelCategory = new Category();
        secondLevelCategory.setName("Citrus");
        secondLevelCategory.setParentCategory(topLevelCategory);
        secondLevelCategory.setLevel(2);

        thirdLevelCategory = new Category();
        thirdLevelCategory.setName("Oranges");
        thirdLevelCategory.setParentCategory(secondLevelCategory);
        thirdLevelCategory.setLevel(3);

        createProductRequest = new CreateProductRequest();
        createProductRequest.setTopLavelCategory("Fruits");
        createProductRequest.setSecondLavelCategory("Citrus");
        createProductRequest.setThirdLavelCategory("Oranges");
        createProductRequest.setTitle("Navel Orange");
        createProductRequest.setColor("Orange");
        createProductRequest.setDescription("Sweet and juicy oranges");
        createProductRequest.setDiscountedPrice(2);
        createProductRequest.setDiscountPersent(5);
        createProductRequest.setImageUrl("http://example.com/image.jpg");
        createProductRequest.setBrand("CitrusCo");
        createProductRequest.setPrice(3);
        createProductRequest.setQuantity(100);

        product = new Product();
        product.setId(1L);
        product.setTitle("Navel Orange");
        product.setColor("Orange");
        product.setDescription("Sweet and juicy oranges");
        product.setDiscountedPrice(2);
        product.setDiscountPersent(5);
        product.setImageUrl("http://example.com/image.jpg");
        product.setBrand("CitrusCo");
        product.setPrice(3);
        product.setQuantity(100);
        product.setCategory(thirdLevelCategory);
        product.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateProduct_FruitsCategory_Success() {
        when(categoryRepository.findByName("Fruits")).thenReturn(topLevelCategory);
        when(categoryRepository.findByNameAndParant("Citrus", "Fruits")).thenReturn(secondLevelCategory);
        when(categoryRepository.findByNameAndParant("Oranges", "Citrus")).thenReturn(thirdLevelCategory);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(createProductRequest);

        assertNotNull(createdProduct);
        assertEquals("Navel Orange", createdProduct.getTitle());
        verify(categoryRepository, times(1)).findByName("Fruits");
        verify(categoryRepository, times(1)).findByNameAndParant("Citrus", "Fruits");
        verify(categoryRepository, times(1)).findByNameAndParant("Oranges", "Citrus");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() throws ProductException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        String result = productService.deleteProduct(1L);

        assertEquals("Product deleted Successfully", result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    void testDeleteProduct_ProductException() throws ProductException {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductException thrown = assertThrows(ProductException.class, () -> {
            productService.deleteProduct(1L);
        });

        assertEquals("product not found with id 1", thrown.getMessage());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() throws ProductException {
        Product updatedProduct = new Product();
        updatedProduct.setQuantity(150);
        updatedProduct.setDescription("Updated description");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        assertEquals(150, result.getQuantity());
        assertEquals("Updated description", result.getDescription());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_ProductException() throws ProductException {
        Product updatedProduct = new Product();
        updatedProduct.setQuantity(150);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductException thrown = assertThrows(ProductException.class, () -> {
            productService.updateProduct(1L, updatedProduct);
        });

        assertEquals("product not found with id 1", thrown.getMessage());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Navel Orange", products.get(0).getTitle());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindProductById_Success() throws ProductException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findProductById(1L);

        assertNotNull(result);
        assertEquals("Navel Orange", result.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindProductById_ProductException() throws ProductException {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductException thrown = assertThrows(ProductException.class, () -> {
            productService.findProductById(1L);
        });

        assertEquals("product not found with id 1", thrown.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindProductByCategory() {
        when(productRepository.findByCategory("Fruits")).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.findProductByCategory("Fruits");

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Navel Orange", products.get(0).getTitle());
        verify(productRepository, times(1)).findByCategory("Fruits");
    }

    @Test
    void testSearchProduct() {
        when(productRepository.searchProduct("Navel Orange")).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.searchProduct("Navel Orange");

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Navel Orange", products.get(0).getTitle());
        verify(productRepository, times(1)).searchProduct("Navel Orange");
    }

}
