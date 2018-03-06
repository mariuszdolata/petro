package petrocar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Start {
	

	public static void main(String[] args) {
		System.out.println("Witaj w aplikacji dla petrocar");
		Properties properties = loadProperties("C:\\petrocar\\config.properties");
		EntityRepository entiryRepository = new EntityRepository(properties);
		System.out.println("Program zakoñczy³ dzia³anie.");
	}
	public static Properties loadProperties(String filePath) {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(filePath);

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

}
