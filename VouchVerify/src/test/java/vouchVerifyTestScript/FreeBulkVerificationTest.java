package vouchVerifyTestScript;

import java.awt.AWTException;
import java.io.File;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import genricLibraries.BaseClass;

public class FreeBulkVerificationTest extends BaseClass {

	@Test
	public void bulkVerificationFlow() throws InterruptedException, AWTException {

		SoftAssert assert1 = new SoftAssert();

		// validate the Bulk Verification page by clicking on bulk verification button
		// in home page
		home.clickOnBulkVerify();
		Thread.sleep(2000);
		assert1.assertEquals(home.getPageHeader(), "Bulk Verification");
		assert1.assertFalse(bulk.getUploadButton().isEnabled());
		assert1.assertEquals(bulk.getBulkContainerName(), "Upload Verification File");

		// uploading the file for bulk verification
		assert1.assertEquals(bulk.getDropExcelContainer().getText(), "Drop Excel File here or click to upload");
		
		/*
		 * Checking the Excel and sample Template got downloaded or not
		 */
		bulk.clickOnSampleExcelButton();
		Thread.sleep(1000);
		bulk.clickOnExcelTemplateButton();
		Thread.sleep(2000);
		// Specify the path where the file is expected to be downloaded
        String downloadFolderPath = "C:\\Users\\Guruprasad V\\Downloads";

        // Check if the file exists in the specified directory
        String sampleFileName = "vouch_verifications_sample_template.xlsx";
        String templateFileName = "vouch_verifications_template.xlsx";
        
        // Check if the sample file exists in the specified directory
        String filePath = downloadFolderPath + File.separator + sampleFileName;
        File downloadedFile = new File(filePath);
        assert1.assertTrue(downloadedFile.exists());
        
        // Check if the template file exists in the specified directory
       filePath = downloadFolderPath + File.separator + templateFileName;
       downloadedFile = new File(filePath); 
       assert1.assertTrue(downloadedFile.exists());

		// Checking the error message by uploading the file having more than 50
		// verification details
		bulk.getDropExcelContainer().click();
		Thread.sleep(2000);
		bulk.uploadFile("D:\\Vouchpay office\\Vouch verify\\BankDetailsFiels\\51 verification.xlsx");
		bulk.getUploadButton().click();
		Thread.sleep(2000);
		assert1.assertEquals("Creating more than 50 verifications at a time is not allowed", bulk.getErrorMessage());
		bulk.clickOnCloseButtonInFileUpload();
		Thread.sleep(2000);

		// Checking the error message by uploading the file having more than 0
		// verification details
		bulk.getDropExcelContainer().click();
		Thread.sleep(2000);
		bulk.uploadFile("D:\\Vouchpay office\\Vouch verify\\BankDetailsFiels\\0 verification.xlsx");
		bulk.getUploadButton().click();
		Thread.sleep(2000);
		assert1.assertEquals("No Data Found!", bulk.getErrorMessage());
		bulk.clickOnCloseButtonInFileUpload();
		Thread.sleep(2000);
		
		//Check the bulk verification pre screen by uploading invalid data in excel
		bulk.getDropExcelContainer().click();
		Thread.sleep(2000);
		bulk.uploadFile("D:\\Vouchpay office\\Vouch verify\\BankDetailsFiels\\invalid.xlsx");
		bulk.getUploadButton().click();
		Thread.sleep(2000);
		assert1.assertEquals("Bulk Verify", home.getPageHeader());
		assert1.assertFalse(bulk.clickOnVerifyButton().isEnabled());
		assert1.assertEquals(5, bulk.getBulkVerificationEntries().size());
		assert1.assertEquals("5 Errors, Kindly make changes and upload the sheet again", bulk.getPreScreenErrorMsg());
		assert1.assertEquals("5", bulk.getPreScreenFooter().split("of ")[1]);
		home.clickOnBackWardButton();
		Thread.sleep(2000);
		
		/*
		 * uploading valid excel file and checking 
		 * checking the entries in bulk pre screen and the footer
		 * checking the header, cancel button, verify button
		 */
		bulk.getDropExcelContainer().click();
		Thread.sleep(2000);
		bulk.uploadFile("D:\\Vouchpay office\\Vouch verify\\BankDetailsFiels\\50 verification.xlsx");
		bulk.getUploadButton().click();
		Thread.sleep(2000);
		assert1.assertEquals("Bulk Verify", home.getPageHeader());
		assert1.assertTrue(bulk.clickOnVerifyButton().isEnabled());
		assert1.assertEquals(50, bulk.getBulkVerificationEntries().size());
		try {
			assert1.assertEquals("", bulk.getPreScreenErrorMsg());
		}catch (Exception e) {
			e.getMessage();
		}
		assert1.assertEquals("50", bulk.getPreScreenFooter().split("of ")[1]);
		assert1.assertTrue(bulk.clickOnCancelButton().isEnabled());
		bulk.clickOnCancelButton().click();
		Thread.sleep(2000);
		assert1.assertEquals(home.getPageHeader(), "Bulk Verification");
		
		/*
		 * verifiying the 50 details 
		 */
		bulk.getDropExcelContainer().click();
		Thread.sleep(2000);
		bulk.uploadFile("D:\\Vouchpay office\\Vouch verify\\BankDetailsFiels\\50 verification.xlsx");
		bulk.getUploadButton().click();
		Thread.sleep(2000);
		bulk.clickOnVerifyButton().click();
		Thread.sleep(10000);
		assert1.assertEquals("Bulk Verify", home.getPageHeader());
		assert1.assertEquals(50, history.getTxnEntries().size());
		bulk.clickOnRefreshButton();
		Thread.sleep(2000);
		assert1.assertEquals("Bulk Verify", home.getPageHeader());
		assert1.assertEquals("50", bulk.getPreScreenFooter().split("of ")[1]);
		bulk.clickOnExportButton();
		bulk.clickOnVerifyMoreButton();
		Thread.sleep(2000);
		assert1.assertEquals(home.getPageHeader(), "Bulk Verification");
		home.clickOnHomePage();
		Thread.sleep(2000);
		assert1.assertEquals(home.getPageHeader(), "Home");
		
		assert1.assertAll();
	}

}
