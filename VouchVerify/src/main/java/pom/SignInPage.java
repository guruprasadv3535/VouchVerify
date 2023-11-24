package pom;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignInPage {

//	Declaration

	// Page header
	@FindBy(xpath = "//h5[contains(@class,'MuiTypography-h5')]")
	private WebElement signInPageHeader;

	// backWard Button
	@FindBy(xpath = "//*[name()='path']")
	private WebElement signInBackWardButton;

	// phone number
	@FindBy(xpath = "//label[text()='Phone Number']/../descendant::input")
	private WebElement phoneNumberField;

	// continue button
	@FindBy(xpath = "//button[text()='Continue']")
	private WebElement continueButton;

	// sign up elements
	@FindBy(xpath = "//label[text()='Enter First Name']/../descendant::input")
	private WebElement enterFirstName;

	@FindBy(xpath = "//label[text()='Enter Last Name']/../descendant::input")
	private WebElement enterLastName;

	@FindBy(xpath = "//label[text()='Email ID(optional)']/../descendant::input")
	private WebElement enterEmailID;

	@FindBy(xpath = "//button[text()='Get OTP']")
	private WebElement getOtpButton;

	// pop element if user not logged In
	@FindBy(xpath = "//button[text()='Yes, Login']")
	private WebElement yesLoginButton;

	@FindBy(xpath = "//button[text()='No, Continue as guest']")
	private WebElement continueAsGuest;
	
	// otp page
	@FindBy(xpath = "//label[text()='Enter OTP']/../descendant::input")
	private WebElement enterOtp;
	
	@FindBy(xpath = "//span[text()='Resend OTP']")
	private WebElement resendOtp;
	
	@FindBy(xpath = "//button[text()='Verify OTP & ']")
	private WebElement verifyOtpButton;
	
	//error messages 
	@FindBy(xpath = "//p[@id='stepper-linear-account-email']")
	private WebElement singleInvalidMsg;
	
	@FindBy(xpath = "(//p[@id='stepper-linear-account-email'])[1]")
	private WebElement firstNameInvalidMsg;
	
	@FindBy(xpath = "(//p[@id='stepper-linear-account-email'])[2]")
	private WebElement lastNameInvalidMsg;
	
	@FindBy(className = "css-qj7j48")
	private WebElement backWardButton;
	

//	Initialization

	public SignInPage(WebDriver driver) {

		PageFactory.initElements(driver, this);
	}

//	Utilization

	/*
	 * to fetch the sign In page header
	 */
	public String getFrameHeader() {
		return signInPageHeader.getText();
	}

	/*
	 * to click on back ward button in sign In page
	 */
	public void clickOnSignInBackWard() {
		signInBackWardButton.click();
	}

	/*
	 * to send phone number
	 */
	public WebElement sendPhoneNumber() {
		//phoneNumberField.click();
		return phoneNumberField;
	}

	/*
	 * to click on continue
	 */
	public void clickOnContinue() throws InterruptedException {
		continueButton.click();
		Thread.sleep(1000);
	}

	// sign up

	/*
	 * to enter first name in sign up
	 */
	public WebElement sendFristName() {
		return enterFirstName;
	}

	/*
	 * to send last name in sign up
	 */
	public WebElement sendLastName() {
		return enterLastName;
	}

	/*
	 * to send email ID in sign up
	 */
	public WebElement sendEmailId() {
		return enterEmailID;
	}

	/*
	 * to click on get OTP
	 */
	public void clickOnGetOtp() throws InterruptedException {
		getOtpButton.click();
		Thread.sleep(1000);
	}

	// pop window elements when user is new

	/*
	 * to click on login
	 */
	public void clickOnYesLogin() throws InterruptedException {
		yesLoginButton.click();
		Thread.sleep(1000);
	}

	/*
	 * to click on continue as guest
	 */
	public void clickOnContinueGuest() throws InterruptedException {
		continueAsGuest.click();
		Thread.sleep(1000);
	}
	
	// otp page
	public String getValueAttributeOfOtpField() {
		return enterOtp.getAttribute("value");
	}
	
	public void clickOnResendOtp() {
		resendOtp.click();
	}
	
	public void clickOnVerifyOtpButton() throws InterruptedException {
		verifyOtpButton.click();
		Thread.sleep(1000);
	}
	
	//error message
	public String getSingleInvalidMsg() {
		return singleInvalidMsg.getText();
	}
	
	public String getFirstNameInvalidMsg() {
		return firstNameInvalidMsg.getText();
	}
	
	public String getLastNameInvalidMsg() {
		return lastNameInvalidMsg.getText();
	}
	
	public void signUpBackWardButton() {
		backWardButton.click();
	}
}
