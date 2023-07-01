package com.example.orderservice.service;

import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.models.Order;
import com.example.orderservice.models.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapFromDto)
                .toList();
        order.setOrderItems(orderLineItems);
        orderRepository.save(order);
    }

    public List<OrderResponse> getOrders() {
        List<Order> orders = (List<Order>) orderRepository.findAll();
        return orders.stream()
                .map(this::mapFromOrderToOrderResponse)
                .toList();
    }

    private OrderLineItem mapFromDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());

        return orderLineItem;
    }

    private OrderResponse mapFromOrderToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderItems()
        );
    }

    public OrderResponse getOneOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return mapFromOrderToOrderResponse(order.get());
    }
}
