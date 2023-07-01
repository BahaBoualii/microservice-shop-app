package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.models.Order;
import com.example.orderservice.models.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapFromDto)
                .toList();

        List<String> skuCodes = orderRequest.getOrderLineItems().stream()
                .map(OrderLineItemDto::getSkuCode)
                .toList();

        // check for availability in inventory service
        InventoryResponse[] response = webClient.get()
                                        .uri("http://localhost:8082/api/inventory", uriBuilder -> uriBuilder.queryParam("skus", skuCodes).build())
                                        .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                        .block();

        boolean allProductsInStock = Arrays.stream(response).allMatch(InventoryResponse::isInStock);

        if(allProductsInStock){
            order.setOrderLineItems(orderLineItems);
            orderRepository.save(order);
        } else {
            throw new IllegalAccessException("Product is not in stock for the moment, please try again later");
        }

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
                order.getOrderLineItems()
        );
    }

    public OrderResponse getOneOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return mapFromOrderToOrderResponse(order.get());
    }
}
