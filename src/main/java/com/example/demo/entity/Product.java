package com.example.demo.entity;

public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private Integer version;


    
    public Product() {
    }

    
    public Product(Long id, String name, Integer stock, Integer version) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.version = version;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }



    
}
