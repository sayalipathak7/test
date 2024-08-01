package com.ecommerce.controller;

import com.ecommerce.exception.CartItemException;
import com.ecommerce.exception.UserException;
import com.ecommerce.modal.CartItem;
import com.ecommerce.modal.User;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.CartItemService;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartItemControllerTest {

    @Mock
    private CartItemService cartItemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartItemController cartItemController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartItemController).build();
    }



    @Test
    void updateCartItemHandler_ShouldUpdateCartItem() throws Exception {
        User user = new User();
        user.setId(1L);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(1L);
        updatedCartItem.setQuantity(2);

        when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
        when(cartItemService.updateCartItem(anyLong(), anyLong(), any(CartItem.class))).thenReturn(updatedCartItem);

        mockMvc.perform(put("/api/cart_items/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\": 2}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(2));

        verify(userService, times(1)).findUserProfileByJwt(anyString());
        verify(cartItemService, times(1)).updateCartItem(anyLong(), anyLong(), any(CartItem.class));
    }


}