package com.example.bankbackend.entity;

import jakarta.persistence.*; // 引入数据库映射相关的工具
import lombok.Data;          // 引入自动生成代码的工具
import java.math.BigDecimal; // 引入专门存钱的高精度数字类型

@Entity      // 告诉 Spring Boot：这个类不是普通的类，它是数据库的一张表
@Table(name = "accounts") // 明确告诉它：对应的数据库表名叫做 "accounts"
@Data        // 超级好用！加上它，你就不用手动去写烦人的 getBalance/setBalance 方法了
public class Account {

    @Id // 告诉程序：这一行是“主键”，也就是每条数据的唯一身份证号
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 告诉程序：这个 ID 是数据库自动增加的（1, 2, 3...）
    private Long id;

    // ⚠️ 重点：数据库里叫 user_id（有下划线），Java 变量习惯用 userId（驼峰命名）
    // 我们用 @Column 来把它们强行“连”在一起
    @Column(name = "user_id")
    private Long userId;

    // 同理，card_number 对应 cardNumber
    @Column(name = "card_number")
    private String cardNumber;

    // ⚠️ 银行系统禁忌：千万不能用 double 或 float，因为它们会丢精度！
    // 必须用 BigDecimal，这样一分钱都不会算错。
    private BigDecimal balance;

    // 状态字段，比如 "ACTIVE" 代表正常，"FROZEN" 代表冻结
    private String status;
}