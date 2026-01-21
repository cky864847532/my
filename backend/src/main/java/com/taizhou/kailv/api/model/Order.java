package com.taizhou.kailv.api.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long customerId;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    // 非持久化字段：订单项
    @TableField(exist = false)
    private List<OrderItem> items;
}
