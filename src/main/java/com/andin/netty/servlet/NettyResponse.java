package com.andin.netty.servlet;

import java.util.HashMap;

import java.util.Map;

import com.andin.netty.utils.ConstantUtil;
import com.google.gson.Gson;

public class NettyResponse {

	/* ---响应状态码--- */
	private String resultCode = null;
	
	/* ---响应结果描述信息--- */
	private String resultMsg = null;
	
	/* ---响应数据--- */
	private Map<String, Object> data = null;
	
	/* ---响应头信息--- */
	private Map<String, String> headers = null;
	
	public NettyResponse() {
		//添加默认的响应编码
		addHeader(ConstantUtil.CONTENT_TYPE, ConstantUtil.APPLICATION_JSON);
		//添加跨域响应参数
		addHeader(ConstantUtil.ACCESS_CONTROL_ALLOW_ORIGIN, ConstantUtil.ACCESS_CONTROL_ALLOW_ORIGIN_VALUE);
		addHeader(ConstantUtil.ACCESS_CONTROL_ALLOW_HEADERS, ConstantUtil.ACCESS_CONTROL_ALLOW_HEADERS_VALUE);
		addHeader(ConstantUtil.ACCESS_CONTROL_ALLOW_METHODS, ConstantUtil.ACCESS_CONTROL_ALLOW_METHODS_VALUE);
	}
	
	public String getStringResult() {
		if(data == null) {
			return "";
		}else {
			return new Gson().toJson(data);
		}
	}
	
	public byte[] getByteResult() {
		return getStringResult().getBytes();
	}

	public void addParamters(String name, Object value) {
		data = getData();
		if(ConstantUtil.RESULT_CODE.equals(name)) {
			resultCode = (String) value;
		}
		if(ConstantUtil.RESULT_MSG.equals(name)) {
			resultMsg = (String) value;
		}
		data.put(name, value);
	}
	
	public Object getParamters(String name) {
		data = getData();
		return data.get(name);
	}
	
	public Map<String, Object> getData() {
		if(data == null) {
			data = new HashMap<String, Object>();
		}
		return data;
	}

	public void setData(Map<String, Object> data) {
		if(data.containsKey(ConstantUtil.RESULT_CODE)) {
			resultCode = (String) data.get(ConstantUtil.RESULT_CODE);
		}
		if(data.containsKey(ConstantUtil.RESULT_MSG)) {
			resultMsg = (String) data.get(ConstantUtil.RESULT_MSG);
		};
		this.data = data;
	}


	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
		this.data = getData();
		data.put(ConstantUtil.RESULT_CODE, resultCode);
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
		this.data = getData();
		data.put(ConstantUtil.RESULT_MSG, resultMsg);
	}
	
	public Map<String, String> getHeaders() {
		if(headers == null) {
			headers = new HashMap<String, String>(); 
		}
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String name, String value) {
		headers = getHeaders();
		headers.put(name, value);
	}
	
	
	/**
	 * 打印响应信息
	 * @return
	 */
	public String getResponseParamInfo() {
		StringBuilder builder = new StringBuilder(500);
		builder.append("\n");
		builder.append("[").append("resultCode").append("]").append("=");
		builder.append("[").append(resultCode).append("]").append("\n");
		builder.append("[").append("resultMsg").append("]").append("=");
		builder.append("[").append(resultMsg).append("]").append("\n");
		builder.append("[").append("data").append("]").append("=");
		builder.append("[").append(new Gson().toJson(data)).append("]").append("\n");
		builder.append("[").append("headers").append("]").append("=");
		builder.append("[").append(new Gson().toJson(headers)).append("]").append("\n");
		return builder.toString();
	}
	
}
