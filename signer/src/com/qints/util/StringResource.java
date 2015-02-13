package com.qints.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public class StringResource {
	
	private static Properties pro = new Properties();
	
	private final static String RESOUCE_FILE_PATH = "com/qints/resource/Signer_zh_CN.properties";

	public static String getStringByLabel(String _labelString){
		String resultString = "";
		try {
			if(pro.isEmpty()){
				URL url = StringResource.class.getClassLoader().getResource(RESOUCE_FILE_PATH);
				InputStream is = (InputStream)url.getContent();
				pro.load(is);
			}
			resultString = pro.getProperty(_labelString);
		} catch (IOException e) {
			com.qints.util.Logger.log(e.getCause()+":"+e.getMessage());
			resultString = _labelString;
		}
		return resultString;
	}
}
