package com.andin.netty.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class PropertiesUtils {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
	private static String configFilePath = "config.properties";
	private static Properties configPros;
	
	static {
		//获取jar项目的觉得路径
		String ROOT_PATH = ToolUtil.getJarRootPath();
		logger.info("***properties load root path is: " + ROOT_PATH);
		configPros = getProps(ROOT_PATH + configFilePath);
	}
	
	public static Properties getProps(String configpath) {
		Properties props = new Properties();		
		InputStream stream = null;
		try {
			stream = new FileInputStream(configpath);
			props.load(stream);
		} catch (Exception e) {
			logger.error("***PropertiesUtils.init method is execute fail: ", e);
		}
		return props;
	}
	
	/**
	 * 根据key从指定文件中配置文件中读取配置,默认获取config文件中的文件
	 * @param key
	 * @param file
	 * @return
	 */
	public static String getProperties(String key, String file) {
		String result = null;
		
		if(StringUtils.isEmpty(file)) {
			result = configPros.getProperty(key);			
		}else {
			if(ConstantUtil.CONFIG_PROPERTIES.equals(file)) {
				result = configPros.getProperty(key);	
			}
		}
		if(StringUtils.isNotEmpty(result)) {
			return result.trim();			
		}else {
			return result;			
		}
	}

	public static void main(String[] args) {
		
		  String key = PropertiesUtils.getProperties("zippass", null);
		  System.out.println(key);
		 
	}

}
