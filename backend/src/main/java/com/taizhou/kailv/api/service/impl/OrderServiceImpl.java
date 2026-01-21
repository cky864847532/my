package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taizhou.kailv.api.mapper.OrderMapper;
import com.taizhou.kailv.api.model.Order;
import com.taizhou.kailv.api.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
