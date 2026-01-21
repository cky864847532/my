package com.taizhou.kailv.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taizhou.kailv.api.model.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}
