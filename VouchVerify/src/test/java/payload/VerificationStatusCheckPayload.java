package payload;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import endpoints.ApiKeyAccess;
import endpoints.PrivateKeyAccess;
import endpoints.SignatureGenerator;
import endpoints.TimeStampGenerator;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class VerificationStatusCheckPayload {

	public static Response verificationStatus(PropertiesUtility property, String verifyRef) {

		JSONObject outerBody = new JSONObject();
		outerBody.put("verification_ref", verifyRef);
		outerBody.put("timestamp", TimeStampGenerator.generateTimestamp());

		ObjectMapper mapper = new ObjectMapper();
		String stringPayload = null;
		try {
			stringPayload = mapper.writeValueAsString(outerBody);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String signature = null;
		try {
			signature = SignatureGenerator.generateRSASignature(stringPayload,
					PrivateKeyAccess.getPrivateKey("./PrivateKey/LivePrivateKey.key"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		not needed for body
		outerBody.put("signature", signature);

//		It is the apikey for header and to access the api
		// live api key
		// String apiKey = ApiKeyAccess.getApiKey("./apiKey/statusApiKey.key");
		// sandbox api key
		String apiKey = ApiKeyAccess.getApiKey("./apiKey/statusApiKey.key");

//		It is for invoking the api 
		Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.header("apikey", apiKey).body(outerBody.toJSONString())
				.baseUri(UtilitiesPath.BASE_URI + "/v1/verify/beneficiary/status").when().post().then()
				.assertThat().statusCode(200).extract().response();

//       .log().body() to print the request body which we sent this should hard coded after baseUri

		return response;
	}

}
