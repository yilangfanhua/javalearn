package com.example.demo.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class UserCreateRequest {
    @NotNull (message = "")
    private Long id;

    @NotBlank(message = "name 不能为空")
    @Size(max = 100, message = "name 长度不能超过 100")
    private String name;

    @NotNull(message = "age 不能为空")
    @Min(value = 0, message = "age 不能小于 0")
    @Max(value = 150, message = "age 不能大于 150")
    private Integer age;

    public UserCreateRequest(){

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


}
