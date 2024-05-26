package com.jeevankumar.order_service.controller;

import com.jeevankumar.order_service.dto.CustomerOrder;
import com.jeevankumar.order_service.dto.OrderEvent;
import com.jeevankumar.order_service.entity.Order;
import com.jeevankumar.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @PostMapping(value = "/orders")
    public void createOrder(@RequestBody CustomerOrder customerOrder){
        Order order = Order.builder()
                .item(customerOrder.getItem())
                .qty(customerOrder.getQty())
                .amount(customerOrder.getAmount())
                .status("CREATED")
                .build();
        try {
            Order savedOrder = this.orderRepository.save(order);
            log.info("Order is saved to database{}",savedOrder.getItem());

            customerOrder.setOrderId(savedOrder.getId());

            OrderEvent orderEvent = OrderEvent.builder()
                    .customerOrder(customerOrder)
                    .type("ORDER_CREATED")
                    .build();

            kafkaTemplate.send("new-orders",orderEvent);
        }catch (Exception exception){
           order.setStatus("FAILED");
           orderRepository.save(order);
        }
    }
}
