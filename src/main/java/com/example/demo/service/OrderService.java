package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.OrderMapper;

@Service 
public class OrderService {
    
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    // 构造器注入
    public OrderService(ProductMapper productMapper, OrderMapper orderMapper) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }



    @Transactional
    public void createOrder(Long productId, int quantity) {
        Product product = productMapper.findById(productId);

        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("购买数量必须大于 0");
        }

        int affectedRows = productMapper.decreaseStock(
                productId,
                quantity,
                product.getVersion()
        );
        
        if (affectedRows == 0) {
            throw new IllegalStateException(
                    "库存不足或商品正在被其他请求修改"
            );
        }
        //throw new RuntimeException("测试事务回滚");
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setStatus("CREATED");
        
        orderMapper.insert(order);
    }
}
