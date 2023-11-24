package vouchVerifyTestScript;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;
import io.restassured.response.Response;
import payload.VerificationStatusCheck;
import payload.VerifyBankAccountPayload;

public class ApiVerificationPerformanceTest {

	@Test
	public void apiVerification() {
		SoftAssert assert1=new SoftAssert();
		
		//creating the instance to property and excel classes and initializing it
		PropertiesUtility property=new PropertiesUtility();
		ExcelUtility excel=new ExcelUtility();
		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);

		for (int i = 0; i <= excel.getLastRowNum("apiAcc"); i++) {
			
			VerifyBankAccountPayload.verifyAccPayload(property,excel.readDataFromExcel("apiAcc", i, 0), excel.readDataFromExcel("apiAcc", i, 1));
			Response response=VerificationStatusCheck.verificationStatus(property);
			
			//to fetch the name on bank for json response
			try {
	            // Create an ObjectMapper instance
	            ObjectMapper objectMapper = new ObjectMapper();

	            // Parse the JSON string
	            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());

	            // Get the value of "status"
	            String status = jsonNode.get("data").get("is_valid").asText();

	            // validating
	            assert1.assertTrue(status.equals("valid")||status.equals("invalid")||status.equals("NA"));
	            assert1.assertEquals(jsonNode.get("data").get("status").asText(), "success");
	            
	            //printing the verification ref if status in NA
	            if(status.equals("NA")) {
	            	System.out.println("Pending Verfication ref : "+property.readData("verifyID"));
	            	System.out.println(response.getBody().asString());
	            } else if(!(jsonNode.get("data").get("status").asText().equals("success"))) {
	            	System.out.println(response.getBody().asString());
	            } else {
	            	System.out.println(response.getBody().asString());
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

		}
		assert1.assertAll();
	}
	
}
