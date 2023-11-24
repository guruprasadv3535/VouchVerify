package endpoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiKeyAccess {
	
//	It is used to access the privateKey from the saved folder

	public static String getApiKey(String path) {
		// Define the path to the private key file
		
		//prod live
		  //String apiKeyPath = "./apiKey/apiKey.key";
		//sandbox
		String apiKeyPath=path;

		// Read the private key file
		String apiKey = null;
		try {
			apiKey = Files.readString(Paths.get(apiKeyPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return apiKey;
	}

}
