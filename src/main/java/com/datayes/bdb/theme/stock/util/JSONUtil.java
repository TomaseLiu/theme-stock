package com.datayes.bdb.theme.stock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
	private static final Logger Log = LoggerFactory.getLogger(JSONUtil.class);
	public static String toJSONString(Object object) {
		return JSON.toJSONString(object);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T parseJSON(String json, Class<T> clazz) {
		try{
			if (clazz == String.class) return (T) json;
			return JSON.parseObject(json, clazz);
		}catch(Exception e){
			Log.error("Json error,Json: {},class: {}",json,clazz,e);
		}
		return null;
	}
}
