package vouchVerifyTestScript;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import genricLibraries.BaseClass;
import genricLibraries.BaseClassWithoutProfile;

public class LoginTest extends BaseClassWithoutProfile {

	@Test
	public void newUserLogin() throws InterruptedException {
		SoftAssert assert1 = new SoftAssert();

		// validating the home page after navigating to application
		assert1.assertEquals(home.getPageHeader(), "Home");

		// click on sign In to sign up/sign In
		home.clickOnSignInButton();

		// validating the sign In page
		assert1.assertEquals(signIn.getFrameHeader(), "Sign in / Sign up");

		// send a new number which is not registered and click on continue
		signIn.sendPhoneNumber().sendKeys(excel.readDataFromExcel("login", 4, 1));
		signIn.clickOnContinue();

		// validating the sing up page when new number is used to sign in
		assert1.assertEquals(signIn.getFrameHeader(), "Enter Details and Signup");

		// entering the details for sign up
		signIn.sendFristName().sendKeys(excel.readDataFromExcel("login", 5, 1));
		signIn.sendLastName().sendKeys(excel.readDataFromExcel("login", 6, 1));
		//signIn.sendEmailId().sendKeys(excel.readDataFromExcel("login", 7, 1));
		signIn.clickOnGetOtp();
		Thread.sleep(2000);

		// validating the otp page after clicking on get otp
		assert1.assertEquals(signIn.getFrameHeader(), "OTP Verification");

		// created loop to enter the valid otp manually by the user
		while (true) {
			if (signIn.getValueAttributeOfOtpField().length() == 6) {
				break;
			} else {
				continue;
			}
		}
		signIn.clickOnVerifyOtpButton();

		// validating the profile is created or not by using profile name
		home.clickOnProfile();
		assert1.assertEquals(excel.readDataFromExcel("login", 5, 1), home.getProfileName());
		Thread.sleep(3000);
		

		assert1.assertAll();
	}

	@Test
	public void existingUserLogin() throws InterruptedException {
		SoftAssert assert1 = new SoftAssert();

		// validating the home page after navigating to application
		assert1.assertEquals(home.getPageHeader(), "Home");

		// click on sign In to sign up/sign In
		home.clickOnSignInButton();

		// validating the sign In page
		assert1.assertEquals(signIn.getFrameHeader(), "Sign in / Sign up");

		// send a already registered number
		signIn.sendPhoneNumber().sendKeys(excel.readDataFromExcel("login", 1, 1));
		Thread.sleep(1000);
		signIn.clickOnContinue();

		// validating the otp page after clicking on get otp
		assert1.assertEquals(signIn.getFrameHeader(), "OTP Verification");

		// created loop to enter the valid otp manually by the user
		while (true) {
			if (signIn.getValueAttributeOfOtpField().length() == 6) {
				break;
			} else {
				continue;
			}
		}
		Thread.sleep(1000);
		signIn.clickOnVerifyOtpButton();
		Thread.sleep(2000);

		// validate it showing a valid profile which is linked with mobile number
		home.clickOnProfile();
		assert1.assertEquals(home.getProfileName(), excel.readDataFromExcel("login", 2, 1));
		Thread.sleep(3000);
		home.clickOnEscapeProfile();
		
		assert1.assertAll();
	}
}
