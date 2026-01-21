package com.taizhou.kailv.api.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("customers")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private String contact;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
}
