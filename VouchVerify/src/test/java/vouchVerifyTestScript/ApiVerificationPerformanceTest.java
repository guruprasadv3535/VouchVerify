package vouchVerifyTestScript;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;
import io.restassured.response.Response;
import payload.VerificationStatusCheckPayload;
import payload.VerifyBankAccountPayload;

public class ApiVerificationPerformanceTest {

	@Test
	public void apiVerification() {
		SoftAssert assert1 = new SoftAssert();

		// creating the instance to property and excel classes and initializing it
		PropertiesUtility property = new PropertiesUtility();
		ExcelUtility excel = new ExcelUtility();
		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);
		
		for (int i = 0; i <= excel.getLastRowNum("apiAcc"); i++) {
			
			//concating the bank
			String upi=excel.readDataFromExcel("apiAcc", i, 0);
//			if(upi.contains("bank")) {
//				
//			}else {
//				upi=upi+"bank";
//			}
			VerifyBankAccountPayload.verifyAccPayload(property,upi ,
					"ifsc");
			
			//updating the verification ref
			excel.writeToExcel("verifyRef1", i, 0, property.readData("verifyID"), UtilitiesPath.EXCEL_PATH);
			//updating the timeStamp
//			excel.writeToExcel("verifyRef", i, 1, property.readData("timeStamp"), UtilitiesPath.EXCEL_PATH);
			System.out.println(i + ": " + property.readData("verifyID"));

//			//to fetch the name on bank for json response
//			try {
//	            // Create an ObjectMapper instance
//	            ObjectMapper objectMapper = new ObjectMapper();
//
//	            // Parse the JSON string
//	            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
//
//	            // Get the value of "status"
//	            String status = jsonNode.get("data").get("is_valid").asText();
//
//	            // validating
//	            assert1.assertTrue(status.equals("valid")||status.equals("invalid")||status.equals("NA"));
//	            assert1.assertEquals(jsonNode.get("data").get("status").asText(), "success");
//	            
//	            //printing the verification ref if status in NA
//	            if(status.equals("NA")) {
//	            	System.out.println("\n"+response.getBody().asString());
//	            } else if(!(jsonNode.get("data").get("status").asText().equals("success"))) {
//	            	System.out.println("\n"+response.getBody().asString());
//	            } else {
//	            	System.out.println(response.getBody().asString());
//	            }
//	            
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }

		}

		// To check the status of verification
//		for (int j = 0; j <= excel.getLastRowNum("verifyRef1"); j++) {
//			Response response = VerificationStatusCheckPayload.verificationStatus(property,
//					excel.readDataFromExcel("verifyRef", j, 0));
//			System.out.println(response.getBody().asString());
//		
//		   // Get the value of "status"
//        String status = jsonNode.get("data").get("is_valid").asText();
//		  String status = jsonNode.get("data").get("name_on_bank").asText();
//		}
        excel.closeExcel();
		assert1.assertAll();
	}

	@Test
	public void benificaryStatusCheck() {
		SoftAssert assert1 = new SoftAssert();

		// creating the instance to property and excel classes and initializing it
		PropertiesUtility property = new PropertiesUtility();
		ExcelUtility excel = new ExcelUtility();
		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);
		
		// To check the status of verification
		for (int j = 0; j <= excel.getLastRowNum("verifyRef1"); j++) {
			Response response = VerificationStatusCheckPayload.verificationStatus(property,
					excel.readDataFromExcel("verifyRef1", j, 0));
			System.out.println(response.getBody().asString());
			
			//to fetch the name on bank for json response
			try {
	            // Create an ObjectMapper instance
	            ObjectMapper objectMapper = new ObjectMapper();

	            // Parse the JSON string
	            JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());

	            // Get the value of "status"
	            String status = jsonNode.get("data").get("is_valid").asText();
	            String name = jsonNode.get("data").get("name_on_bank").asText();
	            excel.writeToExcel("verifyRef1", j, 1, status, UtilitiesPath.EXCEL_PATH);
	            excel.writeToExcel("verifyRef1", j, 2, name, UtilitiesPath.EXCEL_PATH);

	          
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
		}
        excel.closeExcel();
		assert1.assertAll();
	}
}
