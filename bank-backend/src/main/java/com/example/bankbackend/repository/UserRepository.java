package com.example.bankbackend.repository;

import com.example.bankbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 必须要加这一行，Spring Boot 才会自动帮你实现按用户名查找的功能
    User findByUsername(String username);
}