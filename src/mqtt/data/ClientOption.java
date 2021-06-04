package mqtt.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import application.Main;

public class ClientOption {
	public static Properties properties = new Properties();

	public ClientOption() {
		// Read properties file
		try {
			properties.load(new FileInputStream("data/ClientOption.properties"));
		} catch (IOException e) {
		}

		// Write properties file
		try {
			properties.store(new FileOutputStream("data/ClientOption.properties"), null);
		} catch (IOException e) {
		}
	}

	public static void writeClientOption() {
		try {
			properties.store(new FileOutputStream("data/ClientOption.properties"), null);
		} catch (IOException e) {
		}
	}

	public static boolean isBrokerIPEmpty() {
		if(properties.getProperty("brokerIP").equals("")) 
			return true;
		else
			return false;
	}
	
	public static boolean isClientOptionEmpty() {
		 if(properties.getProperty("floor").equals("") ||
				properties.getProperty("classroom").equals("") ||
				properties.getProperty("id").equals(""))
			return true;
		else
			return false;
	}
	
	
	public static void clientInit() {
		String brokerIP = ClientOption.properties.getProperty("brokerIP");
		String floor = ClientOption.properties.getProperty("floor");  
		String classroom = ClientOption.properties.getProperty("classroom"); 
		String id = ClientOption.properties.getProperty("id");
		
		Main.mqttPublish.init(brokerIP, floor, classroom, id);
		
		Main.mqttSubscribe.init(brokerIP, floor, classroom, id);
	}
	
	public static String getClientId() {
		String classroom = ClientOption.properties.getProperty("classroom"); 
		String id = ClientOption.properties.getProperty("id");
		
		return "Engineering/" + classroom+ "/" + id;
	}
}
