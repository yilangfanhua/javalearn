package com.example.demo.security;

import com.example.demo.entity.Account;
import com.example.demo.mapper.AccountMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService
        implements UserDetailsService {

    private final AccountMapper accountMapper;

    public AccountUserDetailsService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Account account =
                accountMapper.findByUserName(username);

        if (account == null) {
            throw new UsernameNotFoundException(
                    "用户不存在：" + username
            );
        }

        UserDetails userDetails = User.withUsername(account.getUsername())
        .password(account.getPassword())
        .roles(account.getRole())
        .build();

        System.out.println(">>> 用户: " + account.getUsername()
                + ", role=" + account.getRole()
                + ", authorities=" + userDetails.getAuthorities());

        return userDetails;
    }
}