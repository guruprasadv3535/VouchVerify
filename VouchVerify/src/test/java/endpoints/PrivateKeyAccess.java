package endpoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrivateKeyAccess {

//	It is used to access the privateKey from the saved folder

	public static String getPrivateKey(String privatePath) {
		// Define the path to the private key file
		String privateKeyPath = privatePath;

		// Read the private key file
		String privateKey = null;
		try {
			privateKey = Files.readString(Paths.get(privateKeyPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return privateKey;
	}

}
