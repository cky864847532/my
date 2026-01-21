package com.taizhou.kailv.api.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("suppliers")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String contact;
    private String phone;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
