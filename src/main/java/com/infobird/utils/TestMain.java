package com.infobird.utils;

public class TestMain {

	public static void main(String[] args) {
		
		String name = PropertiesUtil.getKeyValue("CALL_INFO_HISTORY_TABLE_NAME");
		
		System.out.println(name);
		
	/*	PropertiesUtil p = new PropertiesUtil();
		String value = p.getByKey("CALL_INFO_HISTORY_TABLE_NAME");
		System.out.println(value);*/
	}
}
