package com.tekclover.wms.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class WarehouseInterceptorAppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	WarehouseInterceptor warehouseInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(warehouseInterceptor);
	}
}
