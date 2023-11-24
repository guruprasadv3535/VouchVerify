package pom;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

//	Declaration

	// page header
	@FindBy(xpath = "//h4[contains(@class,'MuiTypography-h4')]")
	private WebElement homePageHeader;

	@FindBy(className = "css-qzbb6d")
	private WebElement termsConditionLink;
	
	@FindBy(className = "css-og18vi")
	private WebElement backWardButton;

	// side nav buttons

	@FindBy(xpath = "//p[text()='Home']/ancestor::a")
	private WebElement sideNavHomeButton;

	@FindBy(xpath = "//p[text()='Bulk Verification']/ancestor::a")
	private WebElement sideNavBulkVerificationButton;

	@FindBy(xpath = "//p[text()='Verification History']/ancestor::a")
	private WebElement sideNavVerificationHistory;

	@FindBy(xpath = "//p[text()='Payment History']/ancestor::a")
	private WebElement sideNavPaymentHistoryButton;

	// sign in button

	@FindBy(xpath = "//button[text()='Sign In']")
	private WebElement signInButton;

	// page center elements

	@FindBy(xpath = "//label[text()='Enter Bank A/c No or UPI']/../descendant::input")
	private WebElement bankAccOrUpiField;

	@FindBy(xpath = "//label[text()='Enter IFSC']/../descendant::input")
	private WebElement ifscInputField;

	@FindBy(xpath = "//button[text()='Verify']")
	private WebElement verifyButton;

	// profile details
	@FindBy(xpath = "//div[@class='MuiAvatar-root MuiAvatar-circular css-11rb4o0']")
	private WebElement profileLogo;

	@FindBy(xpath = "//p[@class='MuiTypography-root MuiTypography-body1 css-wisps']")
	private WebElement profileName;

	@FindBy(xpath = "//*[name()='svg']")
	private WebElement logOutButton;

	@FindBy(xpath = "//div[@class='MuiBackdrop-root MuiBackdrop-invisible MuiModal-backdrop css-18hd20a']")
	private WebElement escapeProfileDetails;

	// Invalid cases
	@FindBy(xpath = "(//p[@id='stepper-linear-account-email'])[1]")
	private WebElement acc_upiInvalidMessage;

	@FindBy(xpath = "(//p[@id='stepper-linear-account-email'])[2]")
	private WebElement ifscInvalidMessage;

	@FindBy(xpath = "//p[@id='stepper-linear-account-email']")
	private WebElement bothAccIfscInvalidMessage;

	// Guest user popup
	@FindBy(className = "css-lf1diw")
	private WebElement headerOfPopUp;

	@FindBy(xpath = "//button[text()='Yes, Login']")
	private WebElement yesLoginButton;

	@FindBy(xpath = "//button[text()='No, Continue as guest']")
	private WebElement noContinueAsGuestButton;
	
	//Banner
	@FindBy(xpath = "//img[@id='Account Verified']")
	private WebElement bannerProfile;
	
	@FindBy(xpath = "//img[@id='free_banner']")
	private WebElement freeBanner;

//	Initialization

	public HomePage(WebDriver driver) {

		PageFactory.initElements(driver, this);
	}

//	Utilization

	/*
	 * to fetch the text of header
	 */
	public String getPageHeader() throws InterruptedException {
		Thread.sleep(1000);
		return homePageHeader.getText();
	}

	public WebElement getTermsConditionLink() {
		return termsConditionLink;
	}
	
	public void clickOnBackWardButton() {
		backWardButton.click();
	}

	// Side navigation buttons

	/*
	 * To click on Home in Side navigation
	 */
	public void clickOnHomePage() {
		while (true) {
			try {
				sideNavHomeButton.click();
				break;
			} catch (Exception e) {
				continue;
			}
		}
	}

	/*
	 * To click on BulkVerification in Side navigation
	 */
	public void clickOnBulkVerify() {
		while (true) {
			try {
				sideNavBulkVerificationButton.click();
				break;
			} catch (Exception e) {
				continue;
			}
		}
	}

	/*
	 * To click on Verification History in Side navigation
	 */
	public void clickOnVerificationHistory() {
		while (true) {
			try {
				sideNavVerificationHistory.click();
				break;
			} catch (Exception e) {
				continue;
			}
		}
	}

	/*
	 * To click on Payment History in Side navigation
	 */
	public void clickOnPaymentHistory() {
		while (true) {
			try {
				sideNavPaymentHistoryButton.click();
				break;
			} catch (Exception e) {
				continue;
			}
		}
	}

	// Side navigation buttons to validate it displayed or not

	/*
	 * returning the element to check it is displayed or not - Home in Side
	 * navigation
	 */
	public WebElement getHomePage() {
		return sideNavHomeButton;
	}

	/*
	 * returning the element to check it is displayed or not - BulkVerification in
	 * Side navigation
	 */
	public WebElement getBulkVerify() {
		return sideNavBulkVerificationButton;
	}

	/*
	 * returning the element to check it is displayed or not - Verification History
	 * in Side navigation
	 */
	public WebElement getVerificationHistory() {
		return sideNavVerificationHistory;
	}

	/*
	 * returning the element to check it is displayed or not - Payment History in
	 * Side navigation
	 */
	public WebElement getPaymentHistory() {
		return sideNavPaymentHistoryButton;
	}

	// sign in button

	/*
	 * To click on Sign In button to sign up/sign in
	 */
	public void clickOnSignInButton() {
		signInButton.click();
	}

	// page center elements

	/*
	 * To send Acc No or Upi for verification purpose
	 * 
	 * @para = accNo_Upi
	 */
	public WebElement sendAccNo_Upi() {
		return bankAccOrUpiField;
	}

	/*
	 * To send ifsc for verification purpose
	 * 
	 * @para = ifscCode
	 */
	public WebElement sendIfscCode() {
		return ifscInputField;
	}

	/*
	 * To click on verify button
	 */
	public void clickOnVerifyButton() throws InterruptedException {
		verifyButton.click();
		Thread.sleep(1000);
	}

	// profile details

	/*
	 * to click on profile
	 */
	public void clickOnProfile() throws InterruptedException {
		profileLogo.click();
		Thread.sleep(1000);
	}

	/*
	 * to get the profile name
	 */
	public String getProfileName() {
		return profileName.getText();
	}

	/*
	 * to logout from account
	 */
	public void clickOnLogOut() {
		logOutButton.click();
	}

	/*
	 * to remove come back from profile details
	 */
	public void clickOnEscapeProfile() throws InterruptedException {
		escapeProfileDetails.click();
		Thread.sleep(1000);
	}

	// Invalid cases

	public String getInvalidMsgOfAcc_Upi() {
		return acc_upiInvalidMessage.getText();
	}

	public String getInvalidMsgOfIfsc() {
		return ifscInvalidMessage.getText();
	}

	public String getBothAccIfscInvalidMessage() {
		return bothAccIfscInvalidMessage.getText();
	}

	// Guest user pop
	public String getPopHeader() {
		return headerOfPopUp.getText();
	}

	public void clickOnYesLogin() {
		yesLoginButton.click();
	}

	public void clickOnContinueAsGest() {
		noContinueAsGuestButton.click();
	}
	
	//banner check
	public WebElement bannerProfile() {
		return bannerProfile;
	}
	
	public WebElement freeBanner() {
		return freeBanner;
	}
}
