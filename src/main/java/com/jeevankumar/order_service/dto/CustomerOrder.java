package com.jeevankumar.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOrder {
    private String item;
    private Integer qty;
    private String paymentMode;
    private Double amount;
    private Long orderId;
    private String address;
}
