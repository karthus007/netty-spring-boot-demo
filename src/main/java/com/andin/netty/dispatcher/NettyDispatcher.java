package com.andin.netty.dispatcher;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.andin.app.config.ApplicationContextUtil;
import com.andin.netty.annotation.NettyController;
import com.andin.netty.annotation.NettyRequestMapping;
import com.andin.netty.servlet.NettyRequest;
import com.andin.netty.servlet.NettyResponse;
import com.andin.netty.utils.ClassUtil;
import com.andin.netty.utils.ConstantUtil;

public class NettyDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(NettyDispatcher.class);
	
	private static ConcurrentHashMap<String, Object> springmvcBeans = new ConcurrentHashMap<String, Object>();

	private static ConcurrentHashMap<String, Object> urlBeans = new ConcurrentHashMap<String, Object>();

	private static ConcurrentHashMap<String, String> urlMethods = new ConcurrentHashMap<String, String>();
	
	static {
		try {
			// 1.获取当前包下的所有的类
			List<Class<?>> classes = ClassUtil.getClasses(ConstantUtil.CONTROLLER_PACKAGE);
			// 2.将扫包范围所有的类,注入到springmvc容器里面，存放在Map集合中 key为默认类名小写，value 对象
			findClassMVCAnnotation(classes);
			// 3.将url映射和方法进行关联
			handlerMapping();
			logger.debug("NettyDispatcher init is success... ");
		} catch (Exception e) {
			logger.error("NettyDispatcher init is error: ", e);
		}
	}
	
	/**
	 * 通过请求对象和默认响应对象获取响应对象的结果
	 * @param request
	 * @param response
	 * @return
	 */
	public static NettyResponse dealWithRequest(NettyRequest request, NettyResponse response) throws Exception {
		// #################处理请求####################
		
		// 1.获取请求url地址
		String uri = request.getUri();
		if (StringUtils.isEmpty(uri)) {
			response.setResultCode(ConstantUtil.EMPTY_URI_ERROR_CODE);
			response.setResultMsg(ConstantUtil.EMPTY_URI_ERROR_MSG);
			return response;
		}
		// 2.从Map集合中获取控制对象
		Object object = urlBeans.get(uri);
		if (object == null) {
			response.setResultCode(ConstantUtil.NOT_FOUND_URI_ERROR_CODE);
			response.setResultMsg(ConstantUtil.NOT_FOUND_URI_ERROR_MSG);
			return response;
		}
		// 3.使用url地址获取方法
		String methodName = urlMethods.get(uri);
		if (StringUtils.isEmpty(methodName)) {
			response.setResultCode(ConstantUtil.NOT_FOUND_URI_ERROR_CODE);
			response.setResultMsg(ConstantUtil.NOT_FOUND_URI_ERROR_MSG);
			return response;
		}
		// 4.使用java的反射机制调用方法
		response = methodInvoke(object, methodName, request, response);
		
		return response;
	}
	
	private static NettyResponse methodInvoke(Object object, String methodName, NettyRequest request, NettyResponse response) {
		try {
			Class<? extends Object> classInfo = object.getClass();
			Method[] methods = classInfo.getMethods();
			
			Method method = null;
			for (Method item : methods) {
				String name = item.getName();
				if(name.equals(methodName)) {
					method = item;
				}
			}
			
			NettyResponse result = (NettyResponse) method.invoke(object, request, response);
			return result;
		} catch (Exception e) {
			logger.error("methodInvoke executed is error: ", e);
			return null;
		}

	}

	// 2.将扫包范围所有的类,注入到springmvc容器里面，存放在Map集合中 key为默认类名小写，value 对象
	public static void findClassMVCAnnotation(List<Class<?>> classes) throws Exception {
		for (Class<?> classInfo : classes) {
			// 判断类上是否有加上注解
			NettyController nettyController = classInfo.getDeclaredAnnotation(NettyController.class);
			if (nettyController != null) {
				// 默认类名是小写
				String beanId = ClassUtil.toLowerCaseFirstOne(classInfo.getSimpleName());
				// 实例化对象
				//Object object = ClassUtil.newInstance(classInfo);
				Object object = ApplicationContextUtil.getBean(classInfo);
				springmvcBeans.put(beanId, object);
			}
		}
	}

	// 3.将url映射和方法进行关联
	public static void handlerMapping() throws Exception{
		// 1.遍历springmvc bean容器 判断类上属否有url映射注解
		for (Map.Entry<String, Object> mvcBean : springmvcBeans.entrySet()) {
			// 2.遍历所有的方法上是否有url映射注解
			// 获取bean的对象
			Object object = mvcBean.getValue();
			// 3.判断类上是否有加url映射注解
			Class<? extends Object> classInfo = object.getClass();
			NettyRequestMapping declaredAnnotation = classInfo.getDeclaredAnnotation(NettyRequestMapping.class);
			String baseUrl = "";
			if (declaredAnnotation != null) {
				// 获取类上的url映射地址
				baseUrl = declaredAnnotation.value();
			}
			// 4.判断方法上是否有加url映射地址
			Method[] declaredMethods = classInfo.getDeclaredMethods();
			for (Method method : declaredMethods) {
				// 判断方法上是否有加url映射注解
				NettyRequestMapping methodNettyRequestMapping = method.getDeclaredAnnotation(NettyRequestMapping.class);
				if (methodNettyRequestMapping != null) {
					String methodUrl = baseUrl + methodNettyRequestMapping.value();
					// springmvc 容器对象 keya:请求地址 ,vlue类
					urlBeans.put(methodUrl, object);
					// springmvc 容器对象 key:请求地址 ,value 方法名称
					urlMethods.put(methodUrl, method.getName());
				}
			}
		}

	}
	
	
}
