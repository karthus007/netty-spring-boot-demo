package com.andin.netty.utils;

/**
 * Netty常量类
 * @author Administrator
 *
 */
public class ConstantUtil {
	
	/********************* 控制器注解扫描包名 *********************/
	
	public static final String CONTROLLER_PACKAGE = "com.andin.server.controller";

	/********************* 请求响应常量 *********************/
	
	public static final String RESULT_CODE = "resultCode";
	
	public static final String RESULT_MSG = "resultMsg";
	
	public static final String CONTENT_TYPE = "content-type";
	
	public static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
	
	public static final String APPLICATION_JSON = "application/json";
	
	public static final String TEXT_PLAIN = "text/plain";
	
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	
	/********************* 请求响应跨域常量 *********************/

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "Origin, X-Requested-With, Content-Type, Accept";

    public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET, POST, PUT, DELETE";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_VALUE = "*";
	
	/********************* 响应状态码 *********************/
	
	public static final String DEFAULT_SUCCESS_CODE = "0000";
	
	public static final String DEFAULT_SUCCESS_MSG = "请求成功";
	
	public static final String DEFAULT_ERROR_CODE = "0001";
	
	public static final String DEFAULT_ERROR_MSG = "请求失败";
	
	public static final String EMPTY_URI_ERROR_CODE = "0002";
	
	public static final String EMPTY_URI_ERROR_MSG = "请求URI为空";
	
	public static final String NOT_FOUND_URI_ERROR_CODE = "0003";
	
	public static final String NOT_FOUND_URI_ERROR_MSG = "请求URI资源没有找到";
	
	/********************* netty常量 *********************/
	
	public static final String SSL_NETTY_STATUS = "ssl_netty_status";
	
	public static final String SSL_NETTY_KEY_NAME = "ssl_netty_key_name";
	
	public static final String SSL_NETTY_KEY_PASSWORD = "ssl_netty_key_password";
	
	public static final String SSL_NETTY_SunX509 = "SunX509";
	
	public static final String SSL_NETTY_JKS = "JKS";
	
	/********************* 系统常量 *********************/
	
	public static final String CONFIG_PROPERTIES = "config";
	
	public static final String TRUE = "true";
	
	public static final String FALSE = "false";
	
}
