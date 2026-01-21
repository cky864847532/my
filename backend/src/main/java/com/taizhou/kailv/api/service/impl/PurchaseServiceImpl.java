package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taizhou.kailv.api.mapper.PurchaseMapper;
import com.taizhou.kailv.api.model.Purchase;
import com.taizhou.kailv.api.service.PurchaseService;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {
}
