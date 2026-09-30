package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Product;



@Mapper 
public interface ProductMapper {
    Product findById(Long id);

    int decreaseStock(
            @Param("id") Long id,
            @Param("quantity") int quantity,
            @Param("version") int version
    );
    
} 
