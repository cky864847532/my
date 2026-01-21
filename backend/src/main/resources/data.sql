INSERT INTO products (name, spec, created_at) VALUES
('示例产品A', '500g/瓶', NOW()),
('示例产品B', '1kg/袋', NOW());

INSERT INTO suppliers (name, contact, phone, created_at) VALUES
('示例供应商', '张三', '13800000000', NOW());

INSERT INTO sales_units (name, contact, phone, created_at) VALUES
('示例销售单位', '李四', '13900000000', NOW());

INSERT INTO purchases (product_id, supplier_id, package_spec, quantity, purchase_price, logistics, created_at) VALUES
(1, 1, '箱(10瓶)', 5, 100.00, '汽运', NOW());

INSERT INTO sales (product_id, sales_unit_id, package_spec, quantity, sale_price, logistics, created_at) VALUES
(1, 1, '箱(10瓶)', 2, 160.00, '快递', NOW());

INSERT INTO inventory_records (product_id, supplier_id, batch, package_spec, quantity, created_at) VALUES
(1, 1, 'BATCH-001', '箱(10瓶)', 50, NOW());
