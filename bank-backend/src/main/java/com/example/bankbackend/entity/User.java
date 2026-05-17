package com.example.bankbackend.entity; // 这里的路径要对应你的实际包名

import jakarta.persistence.*; // 如果是旧版本用 javax.persistence.*
import lombok.Data; // 建议安装 Lombok 插件，省去写 Getter/Setter 的麻烦

@Entity
@Table(name = "users") // ⚠️ 必须和冯浩宇建的表名一模一样
@Data
public class User {

    @Id // 声明这是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 声明是自增主键
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String realName; // 注意：Java 推荐驼峰命名，JPA 会自动对应数据库的 real_name
}
