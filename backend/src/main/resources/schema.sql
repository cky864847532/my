CREATE TABLE IF NOT EXISTS products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  spec VARCHAR(255) NOT NULL DEFAULT '',
  created_at TIMESTAMP,
  CONSTRAINT uk_product_name_spec UNIQUE (name, spec)
);

CREATE TABLE IF NOT EXISTS suppliers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  contact VARCHAR(100),
  phone VARCHAR(50),
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sales_units (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  contact VARCHAR(100),
  phone VARCHAR(50),
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchases (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT,
  supplier_id BIGINT,
  package_spec VARCHAR(255),
  quantity INT,
  purchase_price DECIMAL(12,2),
  logistics VARCHAR(100),
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sales (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT,
  sales_unit_id BIGINT,
  package_spec VARCHAR(255),
  quantity INT,
  sale_price DECIMAL(12,2),
  logistics VARCHAR(100),
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventory_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT,
  supplier_id BIGINT,
  batch VARCHAR(100),
  package_spec VARCHAR(255),
  quantity INT,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_chats (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT,
  message TEXT,
  reply TEXT,
  source VARCHAR(100),
  confidence DOUBLE,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  roles VARCHAR(255),
  created_at TIMESTAMP
);
