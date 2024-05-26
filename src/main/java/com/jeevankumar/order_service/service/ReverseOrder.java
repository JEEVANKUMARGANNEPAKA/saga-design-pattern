package com.jeevankumar.order_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeevankumar.order_service.dto.OrderEvent;
import com.jeevankumar.order_service.entity.Order;
import com.jeevankumar.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReverseOrder {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "reversed-orders",groupId = "orders-group")
    public void reverseOrder(String event){
        System.out.println("Reverse order event:: " + event);
        try {
            OrderEvent orderEvent = new ObjectMapper().readValue(event,OrderEvent.class);
            Optional<Order> optionalOrder = this.orderRepository.findById(orderEvent.getCustomerOrder().getOrderId());
            optionalOrder.ifPresent(order -> {
                order.setStatus("FAILED");
                orderRepository.save(order);
            });

        }catch (Exception e){
            System.out.println("Exception occur while reverting order details: ");

        }
    }
}
