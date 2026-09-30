package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Account;

@Mapper 
public interface AccountMapper {
    
    Account findByUserName(@Param ("username") String username);

    int insert(Account account);
} 
