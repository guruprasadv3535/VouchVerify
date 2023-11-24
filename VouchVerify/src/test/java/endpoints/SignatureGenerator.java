package endpoints;

//       Using java Cryptography signature is generated

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SignatureGenerator {

//       This method is used to generate the signature for authentication

	public static String generateRSASignature(String payload, String privateKeyPEM) throws Exception {
//         Remove the BEGIN and END lines and any whitespace characters
		privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");

		// Base64 decode the private key
		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);

		// Create a PKCS8EncodedKeySpec from the decoded private key bytes
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

		// Initialize a KeyFactory for RSA
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		// Generate a PrivateKey from the key specification
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		// Create a Signature instance using SHA256withRSA
		Signature rsaSignature = Signature.getInstance("SHA256withRSA");

		// Initialize the Signature with the private key
		rsaSignature.initSign(privateKey);

		// Update the Signature with the payload bytes
		rsaSignature.update(payload.getBytes(StandardCharsets.UTF_8));

		// Generate the signature
		byte[] signatureBytes = rsaSignature.sign();

		// Encode the signature as Base64
		String encodedSignature = Base64.getEncoder().encodeToString(signatureBytes);

		return encodedSignature;
	}
}
