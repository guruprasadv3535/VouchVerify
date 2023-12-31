package vouchVerifyTestScript;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import genricLibraries.BaseClass;
import genricLibraries.UtilitiesPath;
import io.restassured.response.Response;
import payload.VerificationStatusCheckPayload;

public class UpiVerificationFlowForPaid extends BaseClass {

	public void paidUpiVerifyFlowScript(String sheetName, int accRowNum) throws InterruptedException {

		// Setting the system config before making cashfree verify (system config is
		// allowed to do)
		// Response response = SysConfPayload.cashFreeConfig(property);

		SoftAssert assert1 = new SoftAssert();

		// validate the home page
		assert1.assertEquals(home.getPageHeader(), "Home");
		Thread.sleep(2000);

		// sending a upi details to verify at cash free
		home.sendAccNo_Upi().sendKeys(excel.readDataFromExcel(sheetName, accRowNum, 1));
		web.scrollToElement(home.getTermsConditionLink());
		Thread.sleep(1000);
		home.clickOnVerifyButton();

		// for guest flow
//		Thread.sleep(2000);
//		assert1.assertEquals(home.getPopHeader(), "Do you want to login to save details");
//		home.clickOnContinueAsGest();

		Thread.sleep(5000);
		// validating the pay&verify page
		assert1.assertEquals(payVerify.getPayVerifyHeader(), "Pay & Verify");
		List<WebElement> rowVD = payVerify.verificationDetails();
		for (int detailsRow = 0; detailsRow < rowVD.size(); detailsRow++) {
			List<WebElement> columnVD = rowVD.get(detailsRow).findElements(By.tagName("div"));
			for (int detailsCol = 0; detailsCol < columnVD.size(); detailsCol++) {

				if (detailsRow == 0) {
					if (detailsCol == 0) {
						assert1.assertEquals(columnVD.get(detailsCol).getText(), "No of verifications");
					} else if (detailsCol == 2) {
						assert1.assertEquals(columnVD.get(detailsCol).getText(), "1");
					}
				} else if (detailsRow == 1) {
					if (detailsCol == 0) {
						assert1.assertEquals(columnVD.get(detailsCol).getText(), "Cost per Verification");
					} else if (detailsCol == 2) {
						assert1.assertEquals(columnVD.get(detailsCol).getText().charAt(1) + "",
								property.readData("accCost"));
					}
				} else if (detailsRow == 2) {
					if (detailsCol == 0) {
						assert1.assertEquals(columnVD.get(detailsCol).getText(), "GST");
					} else if (detailsCol == 2) {
						double gstAmt = (Double.parseDouble(property.readData("gst")) / 100)
								* Double.parseDouble(property.readData("accCost"));
						assert1.assertEquals(Double.parseDouble(columnVD.get(detailsCol).getText().charAt(1) + ""),
								gstAmt);
					}
				} else if (detailsRow == 3) {
					if (detailsCol == 0) {
						assert1.assertEquals(columnVD.get(detailsCol).getText(), "Total to be paid");
					} else if (detailsCol == 2) {
						double gstAmt = (Double.parseDouble(property.readData("gst")) / 100)
								* Double.parseDouble(property.readData("accCost"));
						double accCost = Double.parseDouble(property.readData("accCost"));
						double totalToBePaid = accCost + gstAmt;
						assert1.assertEquals(Double.parseDouble(columnVD.get(detailsCol).getText().charAt(1) + ""),
								totalToBePaid);
					}
				}
			}
		}
		payVerify.clickOnVerifyButton();

		// making payment
		Thread.sleep(2000);
		assert1.assertEquals(driver.getTitle(), "Checkout");
		payVerify.clickOnAddNewUpi();
		payVerify.sendUpiId(property.readData("upiID"));
		payVerify.clickOnConfirm();

		// waiting for details
		while (true) {
			try {
				String timeOutText = verify.getTimeOutScreenText();

				// checking the verified bank detail is updated in verification history
				verify.clickOnTimeOutScreenGoToVerificationHistory();
				Thread.sleep(2000);

				assert1.assertEquals(home.getPageHeader(), "Verification History");
				String vhVerifyDate = null;
				List<WebElement> verificationHistory = history.getTxnEntries();
				for (int vhRows = 0; vhRows < verificationHistory.size(); vhRows++) {
					if (vhRows == 0) {
						List<WebElement> verificationHistoryVertical = verificationHistory.get(vhRows)
								.findElements(By.tagName("td"));
						for (int vhColumns = 0; vhColumns < verificationHistoryVertical.size(); vhColumns++) {
							if (vhColumns == 1) {
								vhVerifyDate = verificationHistoryVertical.get(vhColumns).getText();
							}
							if (vhColumns == 5) {
								String vhVerifyID = verificationHistoryVertical.get(vhColumns).getText();
								property.writeToProperties("verifyID", vhVerifyID, UtilitiesPath.PROPERTIES_PATH);
								System.out.println(vhVerifyID);
							}
							if (vhColumns == 6) {
								String vhVerifyStatus = verificationHistoryVertical.get(vhColumns).getText();
								assert1.assertEquals(vhVerifyStatus, "Pending at Bank");
							}
						}
					}
				}

				// checking the payment made is updated in payment history
				home.clickOnPaymentHistory();
				Thread.sleep(3000);
				assert1.assertEquals(home.getPageHeader(), "Payment History");

				List<WebElement> paymentHistory = history.getTxnEntries();
				for (int phRows = 0; phRows < paymentHistory.size(); phRows++) {
					if (phRows == 0) {
						List<WebElement> paymentHistoryVertical = paymentHistory.get(phRows)
								.findElements(By.tagName("td"));
						for (int phColumns = 0; phColumns < paymentHistoryVertical.size(); phColumns++) {
							if (phColumns == 0) {
								String phPaymentDate = paymentHistoryVertical.get(phColumns).getText();
								assert1.assertEquals(vhVerifyDate, phPaymentDate);
							}
						}
					}
				}

				// checking the status of verification by using api
				Response statusResponse = VerificationStatusCheckPayload.verificationStatus(property,property.readData("verifyID"));
				System.out.println(statusResponse.getBody().asString());
				break;
			} catch (Exception timeOut) {
				try {
					String headerText = verify.verificationHeader().getText();

					assert1.assertEquals(verify.getPaymentStatus(), "Payment successful");
					// fetching the details in verification details
					String verifyID = verify.getVerificationID();
					String paymentID = verify.getPaymentID();
					String accUpi = verify.getAccnoUpi();
					String ifsc = verify.getIfsc();
					String verifyDate = verify.getVerificationDate();
					String verifyName = verify.getNameInBank();

					// validating the details in verification details shown
					assert1.assertEquals(verify.getAccnoUpi(), excel.readDataFromExcel(sheetName, accRowNum, 1));
					assert1.assertEquals(verify.getIfsc(), "-");
					assert1.assertEquals(paymentID, "-");
					assert1.assertNotEquals(verify.getVerificationID(), "-");

					// validating the verification response/status in verification details
					if (sheetName.equals("ValidDetails")) {
						assert1.assertTrue(verify.getValidVerificationStatus().isDisplayed());
					} else if (sheetName.equals("InvalidDetails")) {
						assert1.assertEquals(verify.getVerificationResponse(), "Invalid");
					}

					// write verifyID and payment ID into properties file
					property.writeToProperties("verifyID", verifyID, UtilitiesPath.PROPERTIES_PATH);
					property.writeToProperties("paymentID", paymentID, UtilitiesPath.PROPERTIES_PATH);
					System.out.println(verifyID);

					// checking the verified bank detail is updated in verification history
					verify.clickOnGoToVerifyHistory();
					Thread.sleep(3000);
					assert1.assertEquals(home.getPageHeader(), "Verification History");

					List<WebElement> verificationHistory = history.getTxnEntries();
					for (int vhRows = 0; vhRows < verificationHistory.size(); vhRows++) {
						if (vhRows == 0) {
							List<WebElement> verificationHistoryVertical = verificationHistory.get(vhRows)
									.findElements(By.tagName("td"));
							for (int vhColumns = 0; vhColumns < verificationHistoryVertical.size(); vhColumns++) {
								if (vhColumns == 1) {
									String vhVerifyDate = verificationHistoryVertical.get(vhColumns).getText();
									assert1.assertEquals(vhVerifyDate, verifyDate);
								} else if (vhColumns == 2) {
									String vhVerifyName = verificationHistoryVertical.get(vhColumns).getText();
									assert1.assertEquals(vhVerifyName, verifyName);
								} else if (vhColumns == 3) {
									String vhVerifyUpi = verificationHistoryVertical.get(vhColumns).getText();
									assert1.assertEquals(vhVerifyUpi, accUpi);
								} else if (vhColumns == 4) {
									String vhVerifyIfsc = verificationHistoryVertical.get(vhColumns).getText();
									assert1.assertEquals(vhVerifyIfsc, ifsc);
								}
								if (vhColumns == 5) {
									String vhVerifyID = verificationHistoryVertical.get(vhColumns).getText();
									assert1.assertEquals(vhVerifyID, verifyID);
								} else if (vhColumns == 6) {
									String vhVerifyStatus = verificationHistoryVertical.get(vhColumns).getText();

									if (sheetName.equals("ValidDetails")) {
										assert1.assertEquals(vhVerifyStatus, "Valid");
									} else if (sheetName.equals("InvalidDetails")) {
										assert1.assertEquals(vhVerifyStatus, "Invalid");
									}
								}
							}
						}
					}

					// checking the payment made is updated in payment history
					home.clickOnPaymentHistory();
					Thread.sleep(3000);
					assert1.assertEquals(home.getPageHeader(), "Payment History");

					List<WebElement> paymentHistory = history.getTxnEntries();
					for (int phRows = 0; phRows < paymentHistory.size(); phRows++) {
						if (phRows == 0) {
							List<WebElement> paymentHistoryVertical = paymentHistory.get(phRows)
									.findElements(By.tagName("td"));
							for (int phColumns = 0; phColumns < paymentHistoryVertical.size(); phColumns++) {
								if (phColumns == 0) {
									String phDate = paymentHistoryVertical.get(phColumns).getText();
									assert1.assertEquals(phDate, verifyDate);
								} else if (phColumns == 1) {
									String phDate = paymentHistoryVertical.get(phColumns).getText();
									// assert1.assertEquals(phDate, verifyDate);
								} else if (phColumns == 2) {
									String phPaymentID = paymentHistoryVertical.get(phColumns).getText();
									assert1.assertEquals(paymentID, phPaymentID);
								}
							}
						}
					}
					// checking the status of verification by using api
					Response statusResponse = VerificationStatusCheckPayload.verificationStatus(property,property.readData("verifyID"));
					System.out.println(statusResponse.getBody().asString());
					break;
				} catch (Exception verifyHeader) {
					continue;
				}
			}
		}

		Thread.sleep(2000);
		assert1.assertAll();
	}

	// Valid upi verification
	@Test(priority = 0)
	public void validUpiVerification1() throws InterruptedException {

		paidUpiVerifyFlowScript("ValidDetails", 10);
	}

	@Test(priority = 1)
	public void validUpiVerification2() throws InterruptedException {

		paidUpiVerifyFlowScript("ValidDetails", 11);
	}

	// Invalid upi verification
	@Test(priority = 2)
	public void inValidUpiVerification1() throws InterruptedException {

		paidUpiVerifyFlowScript("InvalidDetails", 10);
	}

	@Test(priority = 3)
	public void inValidUpiVerification2() throws InterruptedException {

		paidUpiVerifyFlowScript("InvalidDetails", 11);
	}

}
