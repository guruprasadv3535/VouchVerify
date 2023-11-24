package pom;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Pay_VerifyPage {

//	Declaration

	@FindBy(xpath = "//*[name()='path']")
	private WebElement payVerifyBackWardButton;

	@FindBy(xpath = "//h4[contains(@class,'MuiTypography-h4')]")
	private WebElement pay_VerifyHeader;

	@FindBy(xpath = "//button[text()='Pay & Verify']")
	private WebElement verifyButton;

	// verification details and cost
	@FindBy(xpath = "(//div[@style='flex: 1 1 auto;'][2]/div)")
	private List<WebElement> verficationDetails;

	// payment page
	@FindBy(xpath = "//div[text()='ADD NEW UPI']")
	private WebElement addNewUpiButton;

	@FindBy(xpath = "//div[contains(@class,'r-dnmrzs')]")
	private WebElement amtPaying;

	@FindBy(xpath = "//input[@placeholder='xyz@abc']")
	private WebElement upiIDfield;

	@FindBy(xpath = "//div[text()='CONFIRM']")
	private WebElement confirmButton;

//	Initialization

	public Pay_VerifyPage(WebDriver driver) {

		PageFactory.initElements(driver, this);
	}

//	Utilization

	public void clickOnPayVerifyBackWardButton() {
		payVerifyBackWardButton.click();
	}

	public String getPayVerifyHeader() {
		return pay_VerifyHeader.getText();
	}

	public void clickOnVerifyButton() {
		verifyButton.click();
	}

	public List<WebElement> verificationDetails() {
		return verficationDetails;
	}

	// payment page

	public void clickOnAddNewUpi() {
		addNewUpiButton.click();
	}

	public String getAmtPaying() {
		return amtPaying.getText();
	}

	public void sendUpiId(String upiID) {
		upiIDfield.sendKeys(upiID);
	}

	public void clickOnConfirm() {
		confirmButton.click();
	}
}
