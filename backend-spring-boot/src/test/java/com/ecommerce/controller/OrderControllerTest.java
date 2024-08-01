package com.ecommerce.controller;

import com.ecommerce.exception.OrderException;
import com.ecommerce.exception.UserException;
import com.ecommerce.modal.Address;
import com.ecommerce.modal.Order;
import com.ecommerce.modal.User;
import com.ecommerce.service.OrderService;
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

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderHandler_Success() throws UserException {
        String jwt = "jwtToken";
        Address address = new Address();
        User user = new User();
        Order order = new Order();

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);
        when(orderService.createOrder(user, address)).thenReturn(order);

        ResponseEntity<Order> response = orderController.createOrderHandler(address, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order, response.getBody());
    }

    @Test
    void testCreateOrderHandler_UserException() throws UserException {
        String jwt = "jwtToken";
        Address address = new Address();

        when(userService.findUserProfileByJwt(jwt)).thenThrow(new UserException("User not found"));

        UserException thrown = assertThrows(UserException.class, () -> {
            orderController.createOrderHandler(address, jwt);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testUsersOrderHistoryHandler_Success() throws OrderException, UserException {
        String jwt = "jwtToken";
        User user = new User();
        user.setId(1L);
        List<Order> orders = Collections.singletonList(new Order());

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);
        when(orderService.usersOrderHistory(user.getId())).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.usersOrderHistoryHandler(jwt);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orders, response.getBody());
    }

    @Test
    void testUsersOrderHistoryHandler_OrderException() throws OrderException, UserException {
        String jwt = "jwtToken";

        when(userService.findUserProfileByJwt(jwt)).thenThrow(new UserException("User not found"));

        UserException thrown = assertThrows(UserException.class, () -> {
            orderController.usersOrderHistoryHandler(jwt);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testFindOrderHandler_Success() throws OrderException, UserException {
        String jwt = "jwtToken";
        Long orderId = 1L;
        User user = new User();
        Order order = new Order();

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);
        when(orderService.findOrderById(orderId)).thenReturn(order);

        ResponseEntity<Order> response = orderController.findOrderHandler(orderId, jwt);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order, response.getBody());
    }

    @Test
    void testFindOrderHandler_UserException() throws OrderException, UserException {
        String jwt = "jwtToken";
        Long orderId = 1L;

        when(userService.findUserProfileByJwt(jwt)).thenThrow(new UserException("User not found"));

        UserException thrown = assertThrows(UserException.class, () -> {
            orderController.findOrderHandler(orderId, jwt);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testFindOrderHandler_OrderException() throws OrderException, UserException {
        String jwt = "jwtToken";
        Long orderId = 1L;
        User user = new User();

        when(userService.findUserProfileByJwt(jwt)).thenReturn(user);
        when(orderService.findOrderById(orderId)).thenThrow(new OrderException("Order not found"));

        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderController.findOrderHandler(orderId, jwt);
        });

        assertEquals("Order not found", thrown.getMessage());
    }
}
