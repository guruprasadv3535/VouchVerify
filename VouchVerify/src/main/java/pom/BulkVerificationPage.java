package pom;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BulkVerificationPage {

//Initialization

	@FindBy(xpath = "//button[text()='Excel Template']")
	private WebElement excelTemplateButton;

	@FindBy(xpath = "//button[text()='Sample Excel']")
	private WebElement sampleExcelButton;

	@FindBy(id = "upload_element")
	private WebElement dropExcel;

	@FindBy(xpath = "//button[text()='Upload']")
	private WebElement uploadButton;

	@FindBy(className = "css-1ox7qh3")
	private WebElement containerName;

	@FindBy(xpath = "//div[contains(@class,'css-r05pyw')]/div")
	private WebElement errorMessage;

	@FindBy(xpath = "//*[local-name()='svg' and @data-testid='CloseIcon']")
	private WebElement closeIcon;

	// pre-bulk verification screen
	@FindBy(xpath = "//tbody[contains(@class,'css-n2t27x')]/tr")
	private List<WebElement> verificationEntries;
	
	@FindBy(className = "css-ibtamv")
	private WebElement preScreenErrorMsg;
	
	@FindBy(xpath = "//button[text()='Verify']")
	private WebElement verifyButton;
	
	@FindBy(className = "css-1ut9wea")
	private WebElement preScreenFooter;
	
	@FindBy(xpath = "//button[text()='Cancel']")
	private WebElement cancelButton;
	
	@FindBy(xpath = "//thead[contains(@class,'css-1y8o0b2')]/tr/th")
	private List<WebElement> tableHeader;
	
	//post bulk verification screen
	@FindBy(xpath = "//button[text()='Export']")
	private WebElement exportButton;
	
	@FindBy(xpath = "//input[@placeholder='Search']")
	private WebElement searchBar;
	
	@FindBy(xpath = "//button[contains(@class,'css-1jua2ba')]")
	private WebElement refreshButton;
	
	@FindBy(xpath = "//button[text()='Verify more A/c or UPI']")
	private WebElement verifyMoreButton;

//	Initialization
	public BulkVerificationPage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}

//	Declaration

	public void clickOnExcelTemplateButton() {
		excelTemplateButton.click();
	}

	public void clickOnSampleExcelButton() {
		sampleExcelButton.click();
	}

	public WebElement getDropExcelContainer() {
		return dropExcel;
	}

	public WebElement getUploadButton() {
		return uploadButton;
	}

	public String getBulkContainerName() {
		return containerName.getText();
	}

	public String getErrorMessage() {
		return errorMessage.getText();
	}

	public void clickOnCloseButtonInFileUpload() {
		closeIcon.click();
	}

	// pre-bulk verification screen
	public List<WebElement> getBulkVerificationEntries() {
		return verificationEntries;
	}

	public String getPreScreenErrorMsg() {
		return preScreenErrorMsg.getText();
	}
	
	public WebElement clickOnVerifyButton() {
		return verifyButton;
	}
	
	public String getPreScreenFooter() {
		return preScreenFooter.getText();
	}
	
	public WebElement clickOnCancelButton() {
		return cancelButton;
	}
	
	public List<WebElement> getPreScreenTableHeader(){
		return tableHeader;
	}
	
	//post bulk verification
	public void clickOnExportButton() {
		exportButton.click();
	}
	
	public void clickOnRefreshButton() {
		refreshButton.click();
	}
	
	public void clickOnVerifyMoreButton() {
		verifyMoreButton.click();
	}
	
	public void searchBarField(String keyValues) {
		searchBar.sendKeys(keyValues);
	}
	
	/*
	 * This method is to upload the file in file pop up
	 */
	public void uploadFile(String filePath) throws AWTException {

		// creating object of Robot class
		Robot rb = new Robot();

		// copying File path to Clipboard
		StringSelection str = new StringSelection(filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);

		// press Contol+V for pasting
		rb.keyPress(KeyEvent.VK_CONTROL);
		rb.keyPress(KeyEvent.VK_V);

		// release Contol+V for pasting
		rb.keyRelease(KeyEvent.VK_CONTROL);
		rb.keyRelease(KeyEvent.VK_V);

		// for pressing and releasing Enter
		rb.keyPress(KeyEvent.VK_ENTER);
		rb.keyRelease(KeyEvent.VK_ENTER);
	}
}
