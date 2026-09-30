package com.example.demo.dto;

import com.example.demo.entity.User;

public class UserResponse {

    private Long id;
    private String name;
    private Integer age;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getAge()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}