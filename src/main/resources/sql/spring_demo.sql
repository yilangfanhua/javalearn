-- 建表：用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

-- 建表：账号表（登录用）
CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- 建表：商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    stock INT NOT NULL,
    version INT NOT NULL DEFAULT 0
);

-- 建表：订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- 初始化数据：用户
INSERT INTO users (id, name, age) VALUES (1, 'Alice', 20)
ON DUPLICATE KEY UPDATE name='Alice', age=20;

INSERT INTO users (id, name, age) VALUES (2, 'Bob', 25)
ON DUPLICATE KEY UPDATE name='Bob', age=25;

-- 初始化数据：商品
INSERT INTO products (id, name, stock, version) VALUES (1, '商品A', 100, 0)
ON DUPLICATE KEY UPDATE stock=100, version=0;

INSERT INTO products (id, name, stock, version) VALUES (2, '商品B', 50, 0)
ON DUPLICATE KEY UPDATE stock=50, version=0;

-- 初始化数据：管理员账号
-- 密码是 BCrypt 加密后的 "admin123"
INSERT INTO app_users (id, username, password, role) VALUES
    (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN')
ON DUPLICATE KEY UPDATE
    password='$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    role='ADMIN';