# 台州凯绿化工有限公司 — 农药销售系统 核心API文档

## 概述
本文件概述系统核心RESTful API，覆盖认证、客户、产品、订单、库存、销售统计与AI对话模块。后端建议采用JWT鉴权，所有受保护接口需在Header中携带`Authorization: Bearer <token>`。

---

## 错误码约定
- 200: 操作成功
- 201: 资源已创建
- 400: 参数错误
- 401: 未授权或Token无效
- 403: 无权限
- 404: 资源未找到
- 500: 服务器错误

---

## 认证 (Auth)

### POST /api/auth/login
- 描述：用户登录，返回JWT token
- 权限：公开
- 请求示例:

```json
{
  "username": "admin",
  "password": "password123"
}
```

- 成功响应:

```json
{
  "token": "eyJhbGci...",
  "expires_in": 3600,
  "user": {"id":1,"name":"管理员"}
}
```

### POST /api/auth/refresh
- 描述：刷新Token
- 权限：需携带有效Refresh Token或当前Token

---

## 客户管理 (Customers)

### GET /api/customers
- 描述：客户列表（支持分页、筛选）
- 权限：需要Auth
- 查询参数：`page`, `size`, `name`, `category`

### GET /api/customers/{id}
- 描述：客户详情

### POST /api/customers
- 描述：新增客户
- 请求示例:

```json
{
  "name": "某农场",
  "type": "经销商",
  "contact": "张三",
  "phone": "13800000000",
  "address": "台州XXX"
}
```

### PUT /api/customers/{id}
- 描述：更新客户信息

### DELETE /api/customers/{id}
- 描述：删除客户（建议软删除）

### GET /api/customers/{id}/contacts
- 描述：获取联系记录（含时间、内容、负责人）

### POST /api/customers/{id}/contacts
- 描述：新增联系记录

---

## 产品管理 (Products)

### GET /api/products
- 描述：产品列表（支持分类、关键字、库存状态筛选）

### GET /api/products/{id}
- 描述：产品详情

### POST /api/products
- 描述：新增产品
- 请求示例:

```json
{
  "name": "杀虫剂A",
  "sku": "P001",
  "category": "杀虫剂",
  "unit": "瓶",
  "price": 120.5,
  "hazard": true,
  "description": "有效成分..."
}
```

### PUT /api/products/{id}
- 描述：更新产品

### POST /api/products/{id}/price
- 描述：调整价格（记录变动人、时间、理由）

---

## 订单管理 (Orders)

### POST /api/orders
- 描述：创建订单（支持多商品、客户信息、开票信息）
- 请求示例:

```json
{
  "customerId": 10,
  "items": [
    {"productId": 1, "qty": 10, "price": 120.5},
    {"productId": 2, "qty": 5, "price": 80}
  ],
  "paymentType": "现金",
  "delivery": {"method":"自提","address":"..."}
}
```

响应: 201 Created，返回订单ID和状态

### GET /api/orders
- 描述：订单列表（支持按状态、客户、日期筛选）

### GET /api/orders/{id}
- 描述：订单详情（含商品明细、操作日志、发货信息）

### PUT /api/orders/{id}/status
- 描述：更新订单状态（如：已创建→已付款→已发货→已完成→已取消）
- 请求示例:

```json
{ "status": "已发货", "note": "物流单号: XXX" }
```

---

## 库存管理 (Inventory)

### GET /api/inventory
- 描述：库存列表，支持按产品、仓库、库存下限筛选

### GET /api/inventory/{productId}
- 描述：单品库存明细（批次、有效期、可用量）

### POST /api/inventory/in
- 描述：入库登记（采购入库/退货）
- 请求示例:

```json
{
  "productId": 1,
  "qty": 100,
  "batch": "B20251201",
  "expireAt": "2027-12-01",
  "type": "采购入库",
  "operatorId": 2
}
```

### POST /api/inventory/out
- 描述：出库登记（销售出库）

### POST /api/inventory/adjust
- 描述：盘点调整（记录差异与原因）

### GET /api/inventory/alerts
- 描述：库存预警列表（低于安全库存阈值）

---

## 销售统计 (Statistics)

### GET /api/stats/sales
- 描述：销售额汇总（按日/周/月/区间）
- 查询参数：`startDate`, `endDate`, `groupBy=day|month|product|customer`

### GET /api/stats/products/top
- 描述：畅销产品排行，参数 `limit`（默认10）

### GET /api/stats/customers/top
- 描述：消费客户排行

---

## AI对话模块 (AI Chat)

### POST /api/ai/chat
- 描述：向第三方AI发送用户问题并返回回答，后端需封装对外API并保存对话记录
- 请求示例:

```json
{
  "customerId": 10,
  "message": "这款杀虫剂的用法和注意事项是什么？",
  "context": {"lastOrderId": 123}
}
```

响应示例:

```json
{
  "reply": "建议按每亩XX毫升稀释...",
  "source": "deepseek",
  "confidence": 0.87,
  "suggestedProducts": [{"id":1,"name":"杀虫剂A","price":120.5}]
}
```

### GET /api/ai/history
- 描述：查询历史对话，支持按客户或订单过滤

---

## 数据模型简表（示例字段）
- Customer: id, name, type, contact, phone, address, created_at
- Product: id, name, sku, category, unit, price, hazard, description, created_at
- Order: id, order_no, customer_id, total_amount, status, created_at
- OrderItem: id, order_id, product_id, qty, price
- InventoryRecord: id, product_id, batch, qty, type, operator_id, note, created_at
- AIChat: id, customer_id, message, reply, source, created_at

---

## 实施建议（简要）
- 鉴权：使用JWT，所有写操作鉴权并记录操作人。
- 日志与审计：订单、库存、价格变动需记录操作日志。
- 敏感信息与合规：对AI回复做敏感词过滤与免责声明提示。
- 接口分页：列表接口统一使用 `page`/`size`，响应包含 `total` 字段。

---

更多详细字段、示例与完整OpenAPI规范（YAML/JSON）我可以继续生成；你希望我把这个文档导出为OpenAPI规范，还是继续扩展每个接口的字段与测试用例？
