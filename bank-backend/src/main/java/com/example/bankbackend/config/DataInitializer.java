package com.example.bankbackend.config;

import com.example.bankbackend.entity.Account;
import com.example.bankbackend.entity.User;
import com.example.bankbackend.repository.AccountRepository;
import com.example.bankbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("chenshun") != null) {
            return;
        }

        User user = new User();
        user.setUsername("chenshun");
        user.setPassword("123456");
        user.setRealName("陈顺");
        userRepository.save(user);

        Account acc1 = new Account();
        acc1.setUserId(user.getId().longValue());
        acc1.setCardNumber("6222 0000 1111 2222");
        acc1.setBalance(new BigDecimal("128000.50"));
        acc1.setStatus("ACTIVE");
        accountRepository.save(acc1);

        Account acc2 = new Account();
        acc2.setUserId(user.getId().longValue());
        acc2.setCardNumber("6222 0000 3333 4444");
        acc2.setBalance(new BigDecimal("86000.00"));
        acc2.setStatus("ACTIVE");
        accountRepository.save(acc2);
    }
}
