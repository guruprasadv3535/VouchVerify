package payload;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import endpoints.ApiKeyAccess;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class SysConfPayload {

	
	public static Response cashFreeConfig(PropertiesUtility property) {
		
		JSONObject outerBody=new JSONObject();
		outerBody.put("timeout_status", "verifying_with_vouch_db");
		outerBody.put("sandbox_timeout_value_for_timeout_status", 0);
		outerBody.put("retry_wait_time", 300);
		outerBody.put("unsupported_bank_ttl" , 1);
		outerBody.put("imps_down" ,false );
		outerBody.put("vv_upi_fee", property.readData("upiCost"));
		outerBody.put("vv_bank_acc_fee", property.readData("accCost"));
		outerBody.put("vv_gst_percentage", property.readData("gst"));
		
		ObjectMapper mapper=new ObjectMapper();
		String stringPayload=null;
		try {
			stringPayload=mapper.writeValueAsString(outerBody);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		String signature=null;
//		try {
//			signature = SignatureGenerator.generateRSASignature(stringPayload, PrivateKeyAccess.getPrivateKey());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		not needed for body
		//outerBody.put("signature", signature);
		

//		It is the apikey for header and to access the api
		String apiKey = ApiKeyAccess.getApiKey("./apiKey/apiKey.key");

//		It is for invoking the api 
		Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
				.header("apikey", apiKey).body(outerBody.toJSONString())
				.baseUri(UtilitiesPath.BASE_URI+"/v1/kyc_config/penny_drop_api_config").when().post().then()
				.assertThat().statusCode(200).extract().response();
		
//       .log().body() to print the request body which we sent this should hard coded after baseUri
		
		return response;
	}
}
