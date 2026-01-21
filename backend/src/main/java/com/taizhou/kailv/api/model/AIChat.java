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
@TableName("ai_chats")
public class AIChat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String message;
    private String reply;
    private String source;
    private Double confidence;
    private LocalDateTime createdAt;
}
