package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taizhou.kailv.api.mapper.ProductMapper;
import com.taizhou.kailv.api.model.Product;
import com.taizhou.kailv.api.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

	@Override
	public boolean existsByNameAndSpec(String name, String spec, Long excludeId) {
		if (name == null || spec == null) {
			return false;
		}
		var wrapper = this.lambdaQuery()
				.eq(Product::getName, name)
				.eq(Product::getSpec, spec);
		if (excludeId != null) {
			wrapper.ne(Product::getId, excludeId);
		}
		return wrapper.count() > 0;
	}

}
