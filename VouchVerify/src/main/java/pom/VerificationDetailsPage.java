package pom;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class VerificationDetailsPage {
	
//	Declaration
	@FindBy(xpath = "//p[text()='Verification Details']")
	private WebElement verificationHeader;
	
	@FindBy(xpath = "//p[contains(@class,'css-13aky8h')]")
	private WebElement paymentSuccesfull;
	
	//Action buttons in verification history
	@FindBy(xpath = "//button[text()='Verify more A/c or UPI']")
	private WebElement verifyMoreButton;
	
	@FindBy(xpath = "//button[text()='Verify in bulk']")
	private WebElement verifyInBulkButton;
	
	@FindBy(xpath = "//button[text()='Go to Verification History']")
	private WebElement goToVerifyHistoryButton;
	
	//details shown in verification
	@FindBy(xpath = "//p[text()='Verification ID']/preceding-sibling::p")
	private WebElement verificationID;
	
	@FindBy(xpath = "//p[text()='Payment ID']/preceding-sibling::p")
	private WebElement paymentID;
	
	@FindBy(xpath = "//p[text()='Bank A/c No / UPI']/preceding-sibling::p")
	private WebElement bankAccNoUpi;
	
	@FindBy(xpath = "//p[text()='IFSC']/preceding-sibling::p")
	private WebElement ifscNo;
	
	@FindBy(xpath = "//p[text()='Verification Details']/following-sibling::p[contains(@class,'css-1ve2y2l')]")
	private WebElement verifiedDateAndTime;
	
	@FindBy(xpath = "//p[text()='Verification Details']/following-sibling::h6[contains(@class,'css-n32yc2')]")
	private WebElement nameInBank;
	
	//verification status
	@FindBy(xpath = "//*[local-name()='svg']/*[local-name()='g']")
	private WebElement validStatus;
	
	@FindBy(xpath = "//div[@class='MuiBox-root css-1pvkr1y']/p[1]")
	private WebElement verificationResponse;
	
	//timeout screen
	@FindBy(xpath = "//p[contains(text(),'Uh-oh, looks like its taking longer than expected.')]")
	private WebElement timeoutScreen;
	
	@FindBy(xpath = "//button[text()='Go to Verification history']")
	private WebElement timeOutGoToVerificationButton;
	
	@FindBy(xpath = "//button[text()='Go back to home']")
	private WebElement timeOutGoToHomeButton;
	
	
//	Initialization
	public VerificationDetailsPage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
//	Utilization
	public WebElement verificationHeader() {
		return verificationHeader;
	}
	
	public String getPaymentStatus() {
		return paymentSuccesfull.getText();
	}
	
	//verification id action button
	public void clickOnVerifyMore() {
		verifyMoreButton.click();
	}
	
	public void clickOnVerifyInBulk() {
		verifyInBulkButton.click();
	}
	
	public void clickOnGoToVerifyHistory() {
		goToVerifyHistoryButton.click();
	}
	
	//to fetch the details in verification details
	public String getVerificationID() {
		return verificationID.getText();
	}
	
	public String getPaymentID() {
		return paymentID.getText();
	}
	
	public String getAccnoUpi() {
		return bankAccNoUpi.getText();
	}
	
	public String getIfsc() {
		return ifscNo.getText();
	}
	
	public String getVerificationResponse() {
		return verificationResponse.getText();
	}
	
	public WebElement getValidVerificationStatus() {
		return validStatus;
	}
	
	public String getNameInBank() {
		return nameInBank.getText();
	}
	
	public String getVerificationDate() {
		return verifiedDateAndTime.getText();
	}
	//timeout screen
	public String getTimeOutScreenText() {
		return timeoutScreen.getText();
	}
	
	public void clickOnTimeOutScreenGoToVerificationHistory() {
		timeOutGoToVerificationButton.click();
	}
	
	public void clickOnTimeOutScreenGoToHome() {
		timeOutGoToHomeButton.click();
	}

}
