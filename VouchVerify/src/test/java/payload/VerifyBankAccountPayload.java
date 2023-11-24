package payload;

import java.util.LinkedHashMap;
import java.util.Map;

import endpoints.ApiKeyAccess;
import endpoints.PrivateKeyAccess;
import endpoints.SignatureGenerator;
import endpoints.TimeStampGenerator;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class VerifyBankAccountPayload {

	public static Response verifyAccPayload(PropertiesUtility property,String accNum,String ifscNum) {

		//JSONObject outerBody = new JSONObject();
		Map<String, Object> outerBody = new LinkedHashMap<>();

		// changing the verification ref
		int verificationRef = Integer.parseInt(property.readData("verify_ref"));
		while (true) {
			verificationRef++;
			break;
		}
		String newValue = "" + verificationRef;
		property.writeToProperties("verify_ref", newValue, UtilitiesPath.PROPERTIES_PATH);
		String verfication_ref = "guruVerify-" + verificationRef;
		property.writeToProperties("verifyID", verfication_ref, UtilitiesPath.PROPERTIES_PATH);
		outerBody.put("verification_ref", verfication_ref);
		outerBody.put("type", "bank_account");
		outerBody.put("upi_id", "");
		outerBody.put("account_number",accNum);
		outerBody.put("ifsc", ifscNum);
		outerBody.put("mobile", "8970486528");
		outerBody.put("name", "Guruprasad");
		outerBody.put("escrow_id", "penny1");
		outerBody.put("timestamp", TimeStampGenerator.generateTimestamp());
		outerBody.put("refund_amount", 1);
		outerBody.put("source_upi", "8970486528@paytm");
		outerBody.put("collect_user_ref", "Guruprasad");

		//converting the LinkedHashMap to json object for signature genration
        String jsonPayload = convertToJson(outerBody);

		String signature = null;
		try {
			signature = SignatureGenerator.generateRSASignature(jsonPayload,
					PrivateKeyAccess.getPrivateKey("./PrivateKey/LivePrivateKey.key"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		adding signature into object
		outerBody.put("signature", signature);
		
		//converting the LinkedHashMap to json object by adding signature
		 String finaljsonPayload = convertToJson(outerBody);

//		It is the apikey for header and to access the api
		String apiKey = ApiKeyAccess.getApiKey("./apiKey/statusApiKey.key");

//		It is for invoking the api 
		Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.header("apikey", apiKey).body(finaljsonPayload)
				.baseUri(UtilitiesPath.BASE_URI + "/v1/verify/beneficiary").when().post().then()
				.assertThat().statusCode(200).extract().response();

//       .log().body() to print the request body which we sent this should hard coded after baseUri

		return response;
	}
	
    //converting the LinkedHashMap to JSON object
    private static String convertToJson(Map<String, Object> payload) {
    	 // For simplicity, converting to a string manually
        StringBuilder jsonBuilder = new StringBuilder("{");

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            jsonBuilder.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                jsonBuilder.append("\"").append(entry.getValue()).append("\",");
            } else {
                jsonBuilder.append(entry.getValue()).append(",");
            }
        }
        // Remove the trailing comma if there are entries
        if (payload.size() > 0) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

}
