package vouchVerifyTestScript;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import genricLibraries.BaseClassWithoutProfile;

public class SignUpPageInvalidCriteriaValidation extends BaseClassWithoutProfile {

	// First name is a required field
	// Last name is a required field
	// special characters not allowed
	// special characters not allowed
	// Invalid email address
	// mobile Should be Numeric and lenght of 10
	// mobile is a required field
	
	@Test
	public void signUpErrorMessageValidation() throws InterruptedException {
		SoftAssert assert1 = new SoftAssert();

		// validating the home page after navigating to application
		assert1.assertEquals(home.getPageHeader(), "Home");

		// click on sign In to sign up/sign In
		home.clickOnSignInButton();
		Thread.sleep(2000);

		// validating the sign In page
		assert1.assertEquals(signIn.getFrameHeader(), "Sign in / Sign up");

		// checking the error message of phone number by null value
		signIn.clickOnContinue();
		assert1.assertEquals(signIn.getSingleInvalidMsg(), "mobile is a required field");

		// checking the error message of phone number by entering invalid value
		signIn.sendPhoneNumber().sendKeys(excel.readDataFromExcel("invalid", 7, 1));
		assert1.assertEquals(signIn.getSingleInvalidMsg(), "mobile Should be Numeric and lenght of 10");
		signIn.clickOnContinue();
		signIn.sendPhoneNumber().sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

		// going to signup page by entering valid number
		signIn.sendPhoneNumber().sendKeys(excel.readDataFromExcel("invalid", 8, 1));
		signIn.clickOnContinue();
		Thread.sleep(2000);

		// validating the sing up page when new number is used to sign in
		assert1.assertEquals(signIn.getFrameHeader(), "Enter Details and Signup");

		// checking the error message of first name and last name by null value
		signIn.clickOnGetOtp();
		assert1.assertEquals(signIn.getFirstNameInvalidMsg(), "First name is a required field");
		assert1.assertEquals(signIn.getLastNameInvalidMsg(), "Last name is a required field");

		// checking the error message of first name by entering invalid data
		signIn.sendFristName().sendKeys(excel.readDataFromExcel("invalid", 9, 1));
		assert1.assertEquals(signIn.getFirstNameInvalidMsg(), "special characters not allowed");
		signIn.sendFristName().sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

		// checking the error message of last name by entering invalid data
		signIn.sendLastName().sendKeys(excel.readDataFromExcel("invalid", 10, 1));
		assert1.assertEquals(signIn.getLastNameInvalidMsg(), "special characters not allowed");
		signIn.sendLastName().sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

		// checking the error message of email Id by entering invalid data
		signIn.sendFristName().sendKeys(excel.readDataFromExcel("invalid", 12, 1));
		signIn.sendLastName().sendKeys(excel.readDataFromExcel("invalid", 13, 1));
		signIn.sendEmailId().sendKeys(excel.readDataFromExcel("invalid", 11, 1));
		signIn.clickOnGetOtp();
		assert1.assertEquals(signIn.getSingleInvalidMsg(), "Invalid email address");
		Thread.sleep(2000);

		// going back to privious page from signup page
		signIn.signUpBackWardButton();
		Thread.sleep(2000);
		assert1.assertEquals(signIn.getFrameHeader(), "Sign in / Sign up");

		// going back to privious page for singin/singup page
		signIn.signUpBackWardButton();
		Thread.sleep(2000);
		assert1.assertEquals(home.getPageHeader(), "Home");

		Thread.sleep(2000);
		assert1.assertAll();

	}
}
