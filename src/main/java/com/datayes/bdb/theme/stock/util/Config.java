package com.datayes.bdb.theme.stock.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Config {
	CONTEXT("context.properties"),
	DATASOURCE("datasource.properties"),
	ANALYZER("analyzer.properties"),
	BUSINESS("business.properties"),
	BATCH("batch.properties"),
	SERVICE("service.properties"),
	API("api.properties"),
	CATEGORY("category.properties");

	private final Logger logger = LoggerFactory.getLogger(Config.class);
	
	private Properties prop = new Properties();
	private static final String CONFIG_DIR = "/";
	private static final String DEFAULT_PROPERTIES_ENCODING = "IOS-8859-1";
	private static final String STANDARD_ENCODING = "UTF-8";

	private Config(String file) {
		try {
			InputStream is = Config.class.getResourceAsStream(CONFIG_DIR + file);
			if (is != null) {
				prop.load(is);
				changeEncoding(STANDARD_ENCODING);
			}
		} catch (IOException e) {
			logger.error("Load config " + file + " error.", e);
		}
	}
	
	public Properties getProperties() {
		return prop;
	}
	
	/**
	 * 获取所有键值对
	 * @return
	 */
	public Map<String, Object> getAll() {
		return getAll(null, null);
	}
	
	public Map<String, Object> getAllWithKeyEncoding(String keyEncoding) {
		return getAll(null, keyEncoding);
	}
	
	public Map<String, Object> getAll(String prefix) {
		return getAll(prefix, null);
	}
	
	/**
	 * 获取所有以prefix前缀为key的键值对
	 * @param prefix
	 * @param keyEncoding
	 * @return
	 */
	public Map<String, Object> getAll(String prefix, String keyEncoding) {
		Enumeration<Object> e = prop.keys();
		Map<String, Object> map = new HashMap<String, Object>();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			if (prefix == null || key.startsWith(prefix)) {
				Object val = prop.get(key);
				if (keyEncoding != null) {
					try {
						key = new String(key.getBytes(DEFAULT_PROPERTIES_ENCODING), keyEncoding);
					} catch (UnsupportedEncodingException e1) {
					}
				}
				map.put(key, val);
			}
		}
		return map;
	}
	public void changeEncoding(String toEncoding){
		Enumeration<Object> e = prop.keys();
		while (e.hasMoreElements()) {
				String key = e.nextElement().toString();
				String val = prop.getProperty(key);
				if (toEncoding != null) {
					String newVal = val;
					try {
						newVal = new String(val.getBytes(STANDARD_ENCODING), toEncoding);
					} catch (UnsupportedEncodingException e1) {
						newVal = val;
					}
					prop.setProperty(key, newVal);
				}
		}
	}
	public String get(String key) {
		return prop.getProperty(key);
	}
	
	public Integer getAsInt(String key) {
		try {
			return Integer.parseInt(get(key));
		} catch (NumberFormatException nfe) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public Integer getAsInt(String key, int defaultVal) {
		Integer val = null;
		try {
			val = Integer.parseInt(get(key));
		} catch (NumberFormatException nfe) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
		return val != null ? val : defaultVal;
	}
	
	public boolean getAsBool(String key) {
		return Boolean.parseBoolean(key);
	}
	
	public Double getAsDouble(String key) {
		try {
			return Double.parseDouble(get(key));
		} catch (NumberFormatException nfe) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public Double getAsDouble(String key, double defaultVal) {
		Double val = null;
		try {
			val = Double.parseDouble(get(key));
		} catch (NumberFormatException nfe) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
		return val != null ? val : defaultVal;
	}
}
