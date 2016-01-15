package com.infobird.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {

	private static Logger LOG = Logger.getLogger("com.infobird.utils.PropertiesUtil");
	 //属性文件的路径   
    static String profilepath="resources/application.properties";
    /**  
    * 采用静态方法  
    */   
    private static Properties props = new Properties();   
    static {   
        try {   
        	InputStream in = ClassLoader.getSystemResourceAsStream(profilepath);
        	System.out.println(ClassLoader.getSystemResource(profilepath));
        	LOG.info("[PropertiesUtil:] [in:]" + in  + "url:" + ClassLoader.getSystemResource(profilepath));
        	if (in != null) {
        		props.load(in);
        	}
        } catch (FileNotFoundException e) {   
            e.printStackTrace();   
            System.exit(-1);   
        } catch (IOException e) {          
            System.exit(-1);   
        }   
    }   
    
    public String getByKey(String key) {
    	InputStream in = this.getClass().getResourceAsStream(profilepath);
    	System.out.println("in:" + in);
    	try {
    		if(in != null) {
			props.load(in);
	        String value = props.getProperty(key);   
	        System.out.println(key +"键的值是："+ value); 
	        return value; 
    		}
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    } 
  
    /**  
    * 读取属性文件中相应键的值  
    * @param key  
    *            主键  
    * @return String  
    */   
    public static String getKeyValue(String key) {   
        return props.getProperty(key);   
    }   
  
    /**  
    * 根据主键key读取主键的值value  
    * @param filePath 属性文件路径  
    * @param key 键名  
    */   
    public static String readValue(String key) {   
        Properties props = new Properties();   
        try {   
        	
			URL url = ClassLoader.getSystemResource(profilepath);
			LOG.info("[PropertiesUtil:] [url:]" + url);
			System.out.println("url:" + url);
        	if(url != null && url.getPath() != null) {
            	String filePath = url.getPath();
                InputStream in = new BufferedInputStream(new FileInputStream(   
                        filePath));   
            	System.out.println(in);
      
            	props.load(in);   
                String value = props.getProperty(key);   
                System.out.println(key +"键的值是："+ value); 
                return value; 
        	} else {
        		return "-1";
        	}
    
        } catch (Exception e) {   
            e.printStackTrace();   
            return null;   
        }   
    }   
      
  
}
