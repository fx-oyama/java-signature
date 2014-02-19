package com.kakakikikeke.sample.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KeyPropertiesManager {

	private static final String DEFAULT_PATH = "key.properties"; 
	private String path;
	private Properties p;
	
	public KeyPropertiesManager(String path) {
		this.setPath(path);
		init();
	}
	
	public KeyPropertiesManager() {
		this.setPath(DEFAULT_PATH);
		init();
	}
	
	private void init() {
		InputStream in = getClass().getResourceAsStream(this.getPath());
		this.p = new Properties();
		try {
			this.p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object get(String key) {
		return this.p.get(key);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
