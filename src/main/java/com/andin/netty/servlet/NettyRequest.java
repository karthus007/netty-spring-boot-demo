package com.andin.netty.servlet;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class NettyRequest {

	/* ---请求路径--- */
	private String uri = null;
	
	/* ---请求IP--- */
	private String hostAddress = null;

	/* ---请求参数--- */
	private Map<String, String> paramters = null;
	
	/* ---请求头信息--- */
	private Map<String, String> headers = null;

	/* ---请求二进制流内容--- */
	private byte[] byteContent = null;
	
	/* ---请求json数据--- */
	private JsonObject jsonContent = null;

	/* ---请求json字符串数据--- */
	private String stringContent = null;

	/* ---请求文件信息--- */
	private Map<String, NettyPart> parts = null;
	
	public String getStringContent() {
		return stringContent;
	}

	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}
	
	public JsonObject getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(JsonObject jsonContent) {
		this.jsonContent = jsonContent;
	}
	
	public Map<String, NettyPart> getParts() {
		if(parts == null) {
			parts = new HashMap<String, NettyPart>(); 
		}
		return parts;
	}
	
	public NettyPart getPart(String name) {
		parts = getParts();
		return parts.get(name);
	}

	public void setParts(Map<String, NettyPart> parts) {
		this.parts = parts;
	}

	public byte[] getByteContent() {
		return byteContent;
	}

	public void setByteContent(byte[] byteContent) {
		this.byteContent = byteContent;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Map<String, String> getParamters() {
		if(paramters == null) {
			paramters = new HashMap<String, String>(); 
		}
		return paramters;
	}
	
	public String getParamter(String name) {
		paramters = getParamters();
		return paramters.get(name);
	}

	public void setParamters(Map<String, String> paramters) {
		this.paramters = paramters;
	}

	public Map<String, String> getHeaders() {
		if(headers == null) {
			headers = new HashMap<String, String>(); 
		}
		return headers;
	}
	
	public String getHeader(String name) {
		headers = getHeaders();
		return headers.get(name);
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	public void addParamter(String name, String value) {
		paramters = getParamters();
		paramters.put(name, value);
	}
	
	public void addHeader(String name, String value) {
		headers = getHeaders();
		headers.put(name, value);
	}
	
	public void addPart(String name, NettyPart part) {
		parts = getParts();
		parts.put(name, part);
	}
	
	/**
	 * 打印请求信息
	 * @return
	 */
	public String getRequestParamInfo() {
		StringBuilder builder = new StringBuilder(500);
		builder.append("\n");
		builder.append("[").append("stringContent").append("]").append("=");
		builder.append("[").append(stringContent).append("]").append("\n");
		builder.append("[").append("paramters").append("]").append("=");
		builder.append("[").append(new Gson().toJson(paramters)).append("]").append("\n");
		builder.append("[").append("headers").append("]").append("=");
		builder.append("[").append(new Gson().toJson(headers)).append("]").append("\n");
		return builder.toString();
	}
	
}
