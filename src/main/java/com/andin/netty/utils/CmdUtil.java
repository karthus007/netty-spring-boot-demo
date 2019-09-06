package com.andin.netty.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class CmdUtil {

	/**
	  * 通过CMD字符串及执行路径执行命令
	 * @param cmd
	 * @param dirPath
	 * @return
	 * @throws Exception 
	 */
	public static boolean executeCmdToResult(String cmd, String dirPath, String charsetName) throws Exception{
		boolean result = false;
		Process process = null;
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = "UTF-8";
		}
		if (StringUtils.isEmpty(dirPath)) {
			process = Runtime.getRuntime().exec(cmd);
		} else {
			process = Runtime.getRuntime().exec(cmd, null, new File(dirPath));
		}
		InputStream is = process.getInputStream();
		InputStreamReader in = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(in);
		@SuppressWarnings("unused")
		String line = "";
		while ((line = reader.readLine()) != null) {
			continue;
		}
		int code = process.waitFor();
		if (code == 0) {
			result = true;
		}
		return result;
	}
	
	

	/**
	 * 通过CMD命令获取执行后的返回结果字符串
	 * @param cmd
	 * @param dirPath
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */
	public static String executeCmdToContent(String cmd, String dirPath, String charsetName) throws Exception{
		String result = "";
		Process process = null;
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = "UTF-8";
		}
		if (StringUtils.isEmpty(dirPath)) {
			process = Runtime.getRuntime().exec(cmd);
		} else {
			process = Runtime.getRuntime().exec(cmd, null, new File(dirPath));
		}
		InputStream successis = process.getInputStream();
		InputStreamReader successin = new InputStreamReader(successis);
		BufferedReader successreader = new BufferedReader(successin);
		StringBuilder successcontent = new StringBuilder();
		String successline = "";
		while ((successline = successreader.readLine()) != null) {
			successcontent.append(successline);
		}
		
		InputStream failis = process.getErrorStream();
		InputStreamReader failin = new InputStreamReader(failis);
		BufferedReader failreader = new BufferedReader(failin);
		StringBuilder failcontent = new StringBuilder();
		String failline = "";
		while ((failline = failreader.readLine()) != null) {
			failcontent.append(failline);
		}
		
		process.waitFor();
		if (successcontent.length() != 0) {
			result = successcontent.toString();
		}else{
			result = failcontent.toString();
		}
		successreader.close();
		failreader.close();
		return result;
	}
	
}
