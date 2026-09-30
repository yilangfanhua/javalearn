package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.OrderRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.OrderService;

@RestController 
@RequestMapping("/orders") 
public class OrderController {


    private final OrderService orderService;

    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ApiResponse<Void> createOrder(@RequestBody OrderRequest request){
        orderService.createOrder(request.getProductId(), request.getQuantity());
        return ApiResponse.success(null);
    }
    
} 