package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taizhou.kailv.api.mapper.InventoryRecordMapper;
import com.taizhou.kailv.api.model.InventoryRecord;
import com.taizhou.kailv.api.service.InventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryRecordMapper, InventoryRecord> implements InventoryService {

}
