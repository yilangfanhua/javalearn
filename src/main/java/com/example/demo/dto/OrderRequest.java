package com.example.demo.dto;

import javax.validation.constraints.NotBlank;


public class OrderRequest {
    @NotBlank(message = "用户名不能为空")
    private Long productId;


    @NotBlank(message = "密码不能为空")
    private int quantity;

    
    public OrderRequest() {
    }


    public Long getProductId() {
        return productId;
    }


    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    

    
    
}
