package com.andin.netty.factory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andin.netty.servlet.NettyPart;
import com.andin.netty.servlet.NettyRequest;
import com.andin.netty.utils.ConstantUtil;
import com.andin.netty.utils.ToolUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class BuilderNettyRequestFactory {
	
    private static final Logger logger = LoggerFactory.getLogger(BuilderNettyRequestFactory.class);
	
    /**
          * 将FullHttpRequest请求转换为自定义的NettyRequest
     * @param req
     * @param decoder
     * @return
     * @throws Exception
     */
	public static NettyRequest builderNettyRequest(FullHttpRequest req, HttpPostRequestDecoder decoder) throws Exception {
		NettyRequest request = new NettyRequest();
		//设置URI
		String uri = req.uri();
		if(uri.contains("?")) {
			request.setUri(uri.substring(0, uri.indexOf("?")));			
		}else {
			request.setUri(uri);		
		}
        
        //为NettyRequest设置请求头
        for(String name : req.headers().names()) {
            for (String value : req.headers().getAll(name)) {
            	request.addHeader(name.toLowerCase(), value);
            }
        }
        
        //解析URI请求路径参数
        QueryStringDecoder decoderQuery = new QueryStringDecoder(req.uri());
        for (Map.Entry<String, List<String>> attr : decoderQuery.parameters().entrySet()) {
            List<String> value = attr.getValue();
            if(value != null) {
            	request.addParamter(attr.getKey(), value.get(0));
            }
        }
        
        //解析请求体的参数
        ContentTypeEnum type = ContentTypeEnum.decideContentType(request);
        if(type == ContentTypeEnum.APPLICATION_JSON || type == ContentTypeEnum.APPLICATION_X_WWW_FORM_URLENCODED) {
        	//解析特殊的包，比如 JSON, 如果是文件，解码到Part内容中了
			if(req.content().isReadable()){
	            //数据类型：  application/json
	            ByteBuf content = req.content();
	            byte[] byteArray = new byte[content.capacity()];  
	            content.readBytes(byteArray); 
	            String jsonString = new String(byteArray, "UTF-8");
	            //判断是否为json格式的数据
	            if(ToolUtil.isJson(jsonString)) {
	            	request.setStringContent(jsonString);
	                JsonObject jsonContent = new JsonParser().parse(jsonString).getAsJsonObject();
	                request.setJsonContent(jsonContent);
	            } 
			}
		}else if(type == ContentTypeEnum.TEXT_PLAIN){
			if(req.content().isReadable()){
				ByteBuf content = req.content();
            	byte[] byteArray = new byte[content.capacity()];  
            	content.readBytes(byteArray); 
            	request.setByteContent(byteArray);
			}
		}else if(type == ContentTypeEnum.MULTIPART_FORM_DATA) {
			//表单提交的数据
			parsePostParameters(request, decoder);	
		}
        
        logger.debug("BuilderNettyRequestFactory.builderNettyRequest method executed is successful...");
        
		return request;
	}
	
	/**
	  * 解析表单提交的参数
	 * @param request
	 * @param decoder
	 */
    private static void parsePostParameters(NettyRequest request, HttpPostRequestDecoder decoder) {
		try {
			Iterator<InterfaceHttpData> iterator = decoder.getBodyHttpDatas().iterator();
			while (iterator.hasNext()) {
				InterfaceHttpData httpData = iterator.next();
				if (httpData == null)
					continue;
				switch (httpData.getHttpDataType()) {
					/** 普通参数 */
					case Attribute:
						String formContent = httpData.toString();
						if(StringUtils.isNotEmpty(formContent)) {
							String[] arr = formContent.split("=");
							if(arr.length == 2) {
								request.addParamter(arr[0], arr[1]);
							}
						}
						break;
					/** 上传的文件 */
					case FileUpload:
						FileUpload fileUpload = (FileUpload) httpData;
						//文件内容
						ByteBuf content = fileUpload.content();
			            byte[] byteArray = new byte[content.capacity()];  
			            content.readBytes(byteArray); 
						//参数名
						String name = fileUpload.getName ();
						//文件名
						String filename = fileUpload.getFilename ();
						//添加Part
						request.addPart(name, new NettyPart(name, filename, byteArray));
						break;
					default:
						break;
				}        	
			}
		}catch (Exception e) {
			logger.error("BuilderNettyRequestFactory.parsePostParameters is error: ", e);
		}
	}
	
    /**
          * 请求类型枚举类
     * @author Administrator
     *
     */
    private enum ContentTypeEnum {
    	
        APPLICATION_JSON, TEXT_PLAIN, MULTIPART_FORM_DATA, APPLICATION_X_WWW_FORM_URLENCODED, UNKOWN_TYPE;
    	
        public static ContentTypeEnum decideContentType(NettyRequest request) {
            Map<String, String> headers = request.getHeaders();
            String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE.toString());
            if(contentType != null) {
            	if(contentType.contains(ConstantUtil.APPLICATION_JSON)){
                    return ContentTypeEnum.APPLICATION_JSON;
                } else if(contentType.contains(ConstantUtil.TEXT_PLAIN)){
                    return ContentTypeEnum.TEXT_PLAIN;
                } else if(contentType.contains(ConstantUtil.APPLICATION_X_WWW_FORM_URLENCODED)) {
                    return ContentTypeEnum.APPLICATION_X_WWW_FORM_URLENCODED;
                } else if(contentType.contains(ConstantUtil.MULTIPART_FORM_DATA)) {
                    return ContentTypeEnum.MULTIPART_FORM_DATA;
    			}
            }
            return ContentTypeEnum.UNKOWN_TYPE;
        }

    }
	
}
