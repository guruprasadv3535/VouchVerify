package vouchVerifyTestScript;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import genricLibraries.BaseClass;

public class HomePageInvalidCriteriaValidation extends BaseClass {

	@Test
	public void homeErrorMessageValidation() throws InterruptedException {

		SoftAssert assert1 = new SoftAssert();
		Thread.sleep(2000);

		// validating the profile name based on number used
		home.clickOnProfile();
		assert1.assertEquals(home.getProfileName(), "GURUPRAS...");
		home.clickOnEscapeProfile();

		// validating the AccNo or upi field without entering any input
		web.scrollToElement(home.getTermsConditionLink());
		Thread.sleep(2000);
		home.clickOnVerifyButton();
		assert1.assertEquals(home.getInvalidMsgOfAcc_Upi(), "This field is required");
		assert1.assertEquals(home.getInvalidMsgOfIfsc(), "IFSC is required for Bank Account Number");

		// validating the AccNo or upi field by entering invalid input(less than 4 characters)
		home.sendAccNo_Upi().sendKeys(excel.readDataFromExcel("invalid", 1, 1));
		home.clickOnVerifyButton();
		assert1.assertEquals(home.getInvalidMsgOfAcc_Upi(), "A/c no must be greater than 3 characters");
		home.sendAccNo_Upi().sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.DELETE);
		Thread.sleep(1000);
		
		// validating the AccNo or upi field by entering invalid input(more than 35 characters)
		home.sendAccNo_Upi().sendKeys(excel.readDataFromExcel("invalid", 2, 1));
		home.clickOnVerifyButton();
		assert1.assertEquals(home.getInvalidMsgOfAcc_Upi(), "A/c no must be less than 36 characters");
		home.sendAccNo_Upi().sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.DELETE);
		Thread.sleep(1000);
		
		// validating the ifsc for valid bank account by clicking on continue without entering ifsc
		home.sendAccNo_Upi().sendKeys(excel.readDataFromExcel("invalid", 3, 1));
		home.clickOnVerifyButton();
		assert1.assertEquals(home.getBothAccIfscInvalidMessage(), "IFSC is required for Bank Account Number");
		home.sendAccNo_Upi().sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.DELETE);
		
		// validating the ifsc field for invalid ifsc entries with valid acc number
		home.sendAccNo_Upi().sendKeys(excel.readDataFromExcel("invalid", 3, 1));
		home.sendIfscCode().sendKeys(excel.readDataFromExcel("invalid", 4, 1));
		home.clickOnVerifyButton();
		assert1.assertEquals(home.getBothAccIfscInvalidMessage(), "Enter a valid IFSC code");
		home.sendAccNo_Upi().sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.DELETE);
		home.sendIfscCode().sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.DELETE);
		
        //check the ifsc field for upi id
		home.sendAccNo_Upi().sendKeys(excel.readDataFromExcel("invalid",5,1));
		assert1.assertFalse(home.sendIfscCode().isEnabled());
		home.sendAccNo_Upi().sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.DELETE);
		
		//check the side navigation components is displayed or not
		assert1.assertTrue(home.getHomePage().isDisplayed(),"Home page menu not displayed");
		assert1.assertTrue(home.getBulkVerify().isDisplayed(),"Bulk verify menu not displayed");
		assert1.assertTrue(home.getVerificationHistory().isDisplayed(),"Verification history menu not displayed");
		assert1.assertTrue(home.getPaymentHistory().isDisplayed(),"Payment history menu not displayed");
		
		//check the banner profile is displayed or not
		assert1.assertTrue(home.bannerProfile().isDisplayed(),"Profile banner not displayed");
		
		//checking the free banner for free version
		assert1.assertTrue(home.freeBanner().isDisplayed(), "Free banner not displayed");
		
		//checking the terms and services link
		assert1.assertEquals(home.getTermsConditionLink().getText(), "Terms of service.");
		home.getTermsConditionLink().click();
		Thread.sleep(2000);
		assert1.assertEquals(home.getPageHeader(), "Terms of Service");
		home.clickOnBackWardButton();
		
		Thread.sleep(2000);
		assert1.assertAll();

	}
}
