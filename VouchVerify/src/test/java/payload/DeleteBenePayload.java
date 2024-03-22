package payload;

import java.util.LinkedHashMap;
import java.util.Map;

import endpoints.ApiKeyAccess;
import genricLibraries.UtilitiesPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DeleteBenePayload {

	public static Response deleteBenePayload(String upiID, String accNum, String ifscNum) {

		// JSONObject outerBody = new JSONObject();
		Map<String, Object> outerBody = new LinkedHashMap<>();

		outerBody.put("upi_id", upiID);
		outerBody.put("account_number", accNum);
		outerBody.put("ifsc", ifscNum);

//				It is the apikey for header and to access the api
		String apiKey = ApiKeyAccess.getApiKey("./apiKey/clientApiKey.key");

//				It is for invoking the api 
		Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.header("apikey", apiKey).body(convertToJson(outerBody))
				.baseUri(UtilitiesPath.BASE_URI + "/v1/verify/beneficiary").when().post().then().assertThat()
				.statusCode(200).extract().response();

//		       .log().body() to print the request body which we sent this should hard coded after baseUri

		return response;
	}

	// converting the LinkedHashMap to JSON object
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
