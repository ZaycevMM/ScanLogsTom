package ru.krista.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	private static Properties properties;
	
	public static Properties getProperties() {
		if (properties == null) {
			new PropertiesUtil();
		}
		return properties;
	}
	
	public PropertiesUtil() {
		init();
	}
		
	private void init (){
		InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties");
		try {
			properties = new Properties();
			properties.load(is);
		} catch (IOException e) {
		}
		try {
			is.close();
		} catch (IOException e) {
		}
	}
}
