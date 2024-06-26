package genricLibraries;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import pom.BulkVerificationPage;
import pom.HomePage;
import pom.Pay_VerifyPage;
import pom.SignInPage;
import pom.VerificationDetailsPage;
import pom.Verification_PaymentHistoryPage;

public class BaseClass {

	protected PropertiesUtility property;
	protected ExcelUtility excel;
	protected WebDriverUtility web;
	protected WebDriver driver;
	protected JavaUtilitiy java;

//	pom classes need to declared
	protected HomePage home;
	protected Pay_VerifyPage payVerify;
	protected SignInPage signIn;
	protected VerificationDetailsPage verify;
	protected Verification_PaymentHistoryPage history;
	protected BulkVerificationPage bulk;
	

//	@BeforeSuite   because there is no database to connect in this project
//	@BeforeTest    because we are not doing parallel execution in this project

	@BeforeClass
	public void classConfig() {
		property = new PropertiesUtility();
		excel = new ExcelUtility();
		web = new WebDriverUtility();
		java=new JavaUtilitiy();

		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);

	}

//	cross browser testing 
    //	@Parameters("browser")
	
	@BeforeMethod
	public void methodConfig() {

		driver = web.launchBrowser(property.readData("browser"));
		
//		for cross browser testing
		//driver=web.launchBrowser(browser);
		
		web.maximizeBrowser();
//		for  live
		web.navigateToApp(property.readData("liveUrl"));
//		for sandbox
		//web.navigateToApp(property.readData("sandBoxUrl"));
		web.waitUntilElementFound(Long.parseLong(property.readData("time")));

		// pom classes need to initialize
		home = new HomePage(driver);
		payVerify = new Pay_VerifyPage(driver);
		signIn = new SignInPage(driver);
		verify = new VerificationDetailsPage(driver);
		history = new Verification_PaymentHistoryPage(driver);
        bulk = new BulkVerificationPage(driver);
	}

	@AfterMethod
	public void methodTearDown() {
		web.quitAllWindows();
	}

	@AfterClass
	public void classTearDown() {
		excel.closeExcel();
	}
//	@AfterTest
//	@AfterSuite
}
