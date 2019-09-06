package com.andin.server.service.impl;

import org.springframework.stereotype.Service;

import com.andin.server.service.TestService;


@Service
//@Transactional
public class TestServiceImpl implements TestService{

	@Override
	public String getAppInfo() {
		return "netty-spring-boot-demo";
	}

}
