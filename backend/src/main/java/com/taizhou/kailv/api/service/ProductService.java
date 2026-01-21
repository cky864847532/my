package com.taizhou.kailv.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taizhou.kailv.api.model.Product;

public interface ProductService extends IService<Product> {
	boolean existsByNameAndSpec(String name, String spec, Long excludeId);
}
