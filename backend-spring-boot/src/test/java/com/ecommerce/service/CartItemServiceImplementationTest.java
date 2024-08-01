package com.ecommerce.service;

import com.ecommerce.exception.CartItemException;
import com.ecommerce.exception.UserException;
import com.ecommerce.modal.Cart;
import com.ecommerce.modal.CartItem;
import com.ecommerce.modal.Product;
import com.ecommerce.modal.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartItemServiceImplementationTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartItemServiceImplementation cartItemService;

    private CartItem cartItem;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize objects for testing
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        product = new Product();
        product.setPrice(100);
        product.setDiscountedPrice(80);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setUserId(user.getId());
        cartItem.setQuantity(1);
        cartItem.setPrice(product.getPrice());
        cartItem.setDiscountedPrice(product.getDiscountedPrice());
    }

    @Test
    void testCreateCartItem() {
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem createdCartItem = cartItemService.createCartItem(cartItem);

        assertNotNull(createdCartItem);
        assertEquals(cartItem.getId(), createdCartItem.getId());
        verify(cartItemRepository, times(1)).save(cartItem);
    }


    @Test
    void testIsCartItemExist() {
        when(cartItemRepository.isCartItemExist(any(Cart.class), any(Product.class), anyString(), anyLong())).thenReturn(cartItem);

        CartItem result = cartItemService.isCartItemExist(new Cart(), product, "M", user.getId());

        assertNotNull(result);
        assertEquals(cartItem.getId(), result.getId());
        verify(cartItemRepository, times(1)).isCartItemExist(any(Cart.class), any(Product.class), anyString(), anyLong());
    }

    @Test
    void testRemoveCartItem_Success() throws CartItemException, UserException {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(userService.findUserById(user.getId())).thenReturn(user);

        cartItemService.removeCartItem(user.getId(), 1L);

        verify(cartItemRepository, times(1)).deleteById(1L);
    }



    @Test
    void testFindCartItemById_Success() throws CartItemException {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItem result = cartItemService.findCartItemById(1L);

        assertNotNull(result);
        assertEquals(cartItem.getId(), result.getId());
    }

    @Test
    void testFindCartItemById_Failure() throws CartItemException {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartItemException.class, () -> {
            cartItemService.findCartItemById(1L);
        });
    }
}
