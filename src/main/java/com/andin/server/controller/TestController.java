package com.andin.server.controller;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.andin.netty.annotation.NettyController;
import com.andin.netty.annotation.NettyRequestMapping;
import com.andin.netty.servlet.NettyPart;
import com.andin.netty.servlet.NettyRequest;
import com.andin.netty.servlet.NettyResponse;
import com.andin.server.service.TestService;
import com.andin.server.utils.ConstantUtil;

@Controller
@NettyController
@NettyRequestMapping("/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	TestService testService;
	
	@NettyRequestMapping("/app")
	public NettyResponse getAppInfo(NettyRequest request, NettyResponse response) {
		logger.debug("TestController.getAppInfo method executed is start...");
		try {
			NettyPart part = request.getPart("file");
			if(part != null) {
				OutputStream os = new FileOutputStream("D:\\" + part.getSubmittedFileName(), true);
				os.write(part.getByteFile());
				os.close();				
			}
			String name = testService.getAppInfo();
			response.addParamters("data", name);
			response.setResultCode(ConstantUtil.DEFAULT_SUCCESS_CODE);
			response.setResultMsg(ConstantUtil.DEFAULT_SUCCESS_MSG);
			logger.debug("TestController.getAppInfo method executed is successful...");
		} catch (Exception e) {
			response.setResultCode(ConstantUtil.DEFAULT_ERROR_CODE);
			response.setResultMsg(ConstantUtil.DEFAULT_ERROR_MSG);
			logger.error("TestController.getAppInfo method executed is failed...", e);
		}
		return response;
	}
	
	
}
