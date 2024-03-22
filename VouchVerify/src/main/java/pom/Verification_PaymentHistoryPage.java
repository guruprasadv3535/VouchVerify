package pom;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Verification_PaymentHistoryPage {

//	Declaration
	@FindBy(xpath = "//label[text()='Date range']/parent::div/descendant::div[@role='button']")
	private WebElement dateRange;

	@FindBy(xpath = "//label[text()='Start Date']/parent::div/descendant::button")
	private WebElement startDateCalender;

	@FindBy(xpath = "//label[text()='End Date']/parent::div/descendant::button")
	private WebElement endDateCalender;

	@FindBy(xpath = "//label[text()='Start Date']/../descendant::input")
	private WebElement startDate;

	@FindBy(xpath = "//label[text()='End Date']/../descendant::input")
	private WebElement endDate;

	@FindBy(xpath = "//button[text()='Apply']")
	private WebElement applyButton;

	@FindBy(xpath = "//li[text()='Recent']")
	private WebElement recentFilter;

	@FindBy(xpath = "//li[text()='Today']")
	private WebElement todayFilter;

	@FindBy(xpath = "//li[text()='Yesterday']")
	private WebElement yesterdayFilter;

	@FindBy(xpath = "//li[text()='Last 7 Days']")
	private WebElement last7DaysFilter;

	@FindBy(xpath = "//li[text()='Last 30 Days']")
	private WebElement last30DaysFilter;

	@FindBy(xpath = "//li[text()='Custom']")
	private WebElement customFilter;

	@FindBy(xpath = "//input[@placeholder='Search']")
	private WebElement searchBar;

	@FindBy(xpath = "//button[contains(@class,'css-1jua2ba')]")
	private WebElement refreshButton;

	@FindBy(xpath = "//button[text()='Export']")
	private WebElement exportButton;

	@FindBy(xpath = "//tbody[@class='MuiTableBody-root css-n2t27x']/tr")
	private List<WebElement> txnList;
	
	@FindBy(xpath = "//thead[contains(@class,'css-1y8o0b2')]/tr/th")
	private List<WebElement> tableHeader;

	@FindBy(xpath = "//p[@class='MuiTypography-root MuiTypography-body2 css-1ut9wea']")
	private WebElement fotterEntriesNumber;

	@FindBy(xpath = "(//button[contains(@class,'css-1xydw8j')])[1]")
	private WebElement priviousPageButton;

	@FindBy(xpath = "(//button[contains(@class,'css-1xydw8j')])[2]")
	private WebElement nextPageButton;
	
	//Verification history accordian
	@FindBy(xpath = "//div[contains(@class,'css-1vgo2f')]/div")
	private List<WebElement> vhLeftAccordian;
	
	@FindBy(xpath = "//div[contains(@class,'css-q74ucs')]/div")
	private List<WebElement> vhRightAccordian;

//	Initialization 
	public Verification_PaymentHistoryPage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}

//	Utilization

	// To click on the date range
	public void clickOnDateRange() {
		dateRange.click();
	}

	// to select the start date calendar
	public void clickOnStartCalendar() {
		startDateCalender.click();
	}

	// to select the end date calendar
	public void clickOnEndCalendar() {
		endDateCalender.click();
	}

	// to retrieve the start date
	public String getStartDate() {
		return startDate.getAttribute("value");
	}

	// to retrieve the end date
	public String getEndDate() {
		return endDate.getAttribute("value");
	}

	// to select the date range
	public void selectDateRange(String filter) {

		dateRange.click();

		switch (filter) {
		case "Recent":
			recentFilter.click();
			break;
		case "Today":
			todayFilter.click();
			break;
		case "Yesterday":
			yesterdayFilter.click();
			break;
		case "Last 7 days":
			last7DaysFilter.click();
			break;
		case "Last 30 days":
			last30DaysFilter.click();
			break;
		case "Custom":
			customFilter.click();
			break;

		default:
			System.out.println("Invalid selection");
			break;
		}
	}

	// to click on apply button
	public void clickOnApplyButton() {
		applyButton.click();
	}

	// return searchBar WebElement to click on it and to search
	public WebElement getSearchBarElement() {
		return searchBar;
	}

	// to refresh the page
	public void clickOnRefreshButton() {
		refreshButton.click();
	}

	// to export the entries in table
	public void clickOnExportButton() {
		exportButton.click();
	}

	// to get the entries in table
	public List<WebElement> getTxnEntries() {
		return txnList;
	}

	// to fetch the entries shown in fotter
	public String getFotterEntriesNumber() {
		return fotterEntriesNumber.getText();
	}

	// to navigate to privious page
	public void clickOnpriviousButton() {
		priviousPageButton.click();
	}

	// to navigate to next page
	public void clickOnNextButton() {
		nextPageButton.click();
	}
	
	//To get the details in verification history accordian
	public List<WebElement> getVHLeftAccordian(){
		return vhLeftAccordian;
	}
	
	public List<WebElement> getVHRightAccordian(){
		return vhRightAccordian;
	}

}
