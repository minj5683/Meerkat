package mqtt.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginInformation {
	public static Properties properties = new Properties();

	public LoginInformation() {
		// Read properties file
		try {
			properties.load(new FileInputStream("data/LoginInformation.properties"));
		} catch (IOException e) {
		}

		// Write properties file
		try {
			properties.store(new FileOutputStream("data/LoginInformation.properties"), null);
		} catch (IOException e) {
		}
	}

	public static void writeLoginInformation() {
		try {
			properties.store(new FileOutputStream("data/LoginInformation.properties"), null);
		} catch (IOException e) {
		}
	}
}
