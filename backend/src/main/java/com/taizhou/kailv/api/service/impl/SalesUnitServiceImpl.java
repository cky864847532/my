package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taizhou.kailv.api.mapper.SalesUnitMapper;
import com.taizhou.kailv.api.model.SalesUnit;
import com.taizhou.kailv.api.service.SalesUnitService;
import org.springframework.stereotype.Service;

@Service
public class SalesUnitServiceImpl extends ServiceImpl<SalesUnitMapper, SalesUnit> implements SalesUnitService {
}
