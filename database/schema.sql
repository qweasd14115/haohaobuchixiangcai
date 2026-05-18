-- 顺顺银行数据库建表脚本
-- 适用于 MySQL 8.0+ 或 H2 (MySQL mode)

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    real_name   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS accounts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    card_number VARCHAR(255),
    balance     DECIMAL(38,2) DEFAULT 0.00,
    status      VARCHAR(50)  DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    type        VARCHAR(50),
    amount      DECIMAL(38,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
