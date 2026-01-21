package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taizhou.kailv.api.mapper.SaleMapper;
import com.taizhou.kailv.api.model.Sale;
import com.taizhou.kailv.api.service.SaleService;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl extends ServiceImpl<SaleMapper, Sale> implements SaleService {
}
