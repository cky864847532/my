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
@TableName("sales")
public class Sale {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("product_id")
    private Long productId;
    @TableField("sales_unit_id")
    private Long salesUnitId;
    @TableField("package_spec")
    private String packageSpec;
    private Integer quantity;
    @TableField("sale_price")
    private Double salePrice;
    private String logistics;
    @TableField("created_at")
    private LocalDateTime createdAt;
}
