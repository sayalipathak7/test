package com.ecommerce.service;

import com.ecommerce.exception.OrderException;
import com.ecommerce.modal.*;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.user.domain.OrderStatus;
import com.ecommerce.user.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplementationTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImplementation orderService;

    private User user;
    private Address shippingAddress;
    private Cart cart;
    private CartItem cartItem;
    private OrderItem orderItem;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize objects for testing
        user = new User();
        user.setId(1L);
        user.setAddresses(new ArrayList<>());

        shippingAddress = new Address();
        shippingAddress.setUser(user);

        cartItem = new CartItem();
        cartItem.setPrice(10);
        cartItem.setDiscountedPrice(8);
        cartItem.setQuantity(2);
        cartItem.setProduct(new Product());

        cart = new Cart();
        cart.setTotalPrice(20.0);
        cart.setTotalDiscountedPrice(16);
        cart.setDiscounte(4);
        cart.setTotalItem(2);
        cart.setCartItems(Set.of(cartItem));

        orderItem = new OrderItem();
        orderItem.setPrice(10);
        orderItem.setDiscountedPrice(8);
        orderItem.setQuantity(2);
        orderItem.setProduct(new Product());

        order = new Order();
        order.setUser(user);
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(20.0);
        order.setTotalDiscountedPrice(16);
        order.setDiscounte(4);
        order.setTotalItem(2);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.getPaymentDetails().setStatus(PaymentStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
    }
    @Test
    void testPlacedOrder_OrderException() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderService.placedOrder(1L);
        });

        assertEquals("order not exist with id 1", thrown.getMessage());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testConfirmedOrder() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.confirmedOrder(1L);

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(updatedOrder);
    }

    @Test
    void testShippedOrder() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.shippedOrder(1L);

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.SHIPPED, updatedOrder.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(updatedOrder);
    }

    @Test
    void testDeliveredOrder() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.deliveredOrder(1L);

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.DELIVERED, updatedOrder.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(updatedOrder);
    }

    @Test
    void testCancelledOrder() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.cancledOrder(1L);

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.CANCELLED, updatedOrder.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(updatedOrder);
    }



    @Test
    void testFindOrderById_OrderException() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderService.findOrderById(1L);
        });

        assertEquals("order not exist with id 1", thrown.getMessage());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testUsersOrderHistory() {
        when(orderRepository.getUsersOrders(1L)).thenReturn(List.of(order));

        List<Order> orders = orderService.usersOrderHistory(1L);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).getUsersOrders(1L);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testDeleteOrder_Success() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteOrder_OrderException() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        OrderException thrown = assertThrows(OrderException.class, () -> {
            orderService.deleteOrder(1L);
        });

        assertEquals("order not exist with id 1", thrown.getMessage());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).deleteById(1L);
    }
}
