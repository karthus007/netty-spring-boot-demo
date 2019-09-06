package com.andin.netty.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ToolUtil {

	/**
	 * 获取项目jar的根路径
	 * @return
	 */
	public static String getJarRootPath() {
		String path = System.getProperty("user.dir").replace("\\", "/");
		//jar服务器部署环境
		//path = path + "/config";
		//开发环境的路径
		path = path + "/src/main/resources";
		return path + File.separator;
	}
	
	/**
	 * 判断字符串是否为json格式的字符串
	 * @param content
	 * @return
	 */
	public static boolean isJson(String content) {
		JsonElement jsonElement = null;
        try {
            jsonElement = new JsonParser().parse(content);
            if (jsonElement == null) {
            	return false;
            }
            if (!jsonElement.isJsonObject()) {
            	return false;
            }
            return true;            
        } catch (Exception e) {
            return false;
        }
	 }
	
	/**
	 * 获取当前时间格式为yyyyMMddHHmmss的时间字符串
	 * @return
	 */
	public static String getCurrentTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(new Date());
	}
	
}
