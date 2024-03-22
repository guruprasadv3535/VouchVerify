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

public class UpiVerificationFlowForFree extends BaseClass {

	public void freeUpiVerifyFlowScript(String sheetName, int accRowNum) throws InterruptedException {

		// Setting the system config before making cashfree verify (system config is
		// allowed to do)
		// Response response = SysConfPayload.cashFreeConfig(property,boolean impsDown);

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
//		assert1.assertEquals(home.getPopHeader(), "Don't want to lose your data? Login to save");
//		home.clickOnContinueAsGest();

		// Directly it will go to verification details page because of free version
		Thread.sleep(5000);

		// waiting for details
		while (true) {
			try {
				String timeOutText = verify.getTimeOutScreenText();

				// checking the verified bank detail is updated in verification history
				verify.clickOnTimeOutScreenGoToVerificationHistory();
				Thread.sleep(2000);

				assert1.assertEquals(home.getPageHeader(), "Verification History");
				String vhVerifyDate = null;
				String vhVerifyID = null;
				String vhVerifyStatus = null;
				String accUpi = null;
				String ifsc = null;
				List<WebElement> verificationHistory = history.getTxnEntries();
				for (int vhRows = 0; vhRows <=0; vhRows++) {
					
						List<WebElement> verificationHistoryVertical = verificationHistory.get(vhRows)
								.findElements(By.tagName("td"));
						for (int vhColumns = 0; vhColumns < verificationHistoryVertical.size(); vhColumns++) {
							if (vhColumns == 1) {
								vhVerifyDate = verificationHistoryVertical.get(vhColumns).getText();
								String currentDate=java.getCurrentTime();
								assert1.assertTrue(vhVerifyDate.contains(currentDate));
							}else if (vhColumns == 2) {
								String name = verificationHistoryVertical.get(vhColumns).getText();
								assert1.assertEquals("-", name);
							}else if (vhColumns == 3) {
								accUpi = verificationHistoryVertical.get(vhColumns).getText();
								assert1.assertEquals(excel.readDataFromExcel(sheetName, accRowNum, 1), accUpi);
							}else if (vhColumns == 4) {
								ifsc = verificationHistoryVertical.get(vhColumns).getText();
								assert1.assertEquals("-", ifsc);
							}else if (vhColumns == 5) {
								vhVerifyID = verificationHistoryVertical.get(vhColumns).getText();
								property.writeToProperties("verifyID", vhVerifyID, UtilitiesPath.PROPERTIES_PATH);
								System.out.println("Pending VerID: " + vhVerifyID);
							}else if (vhColumns == 6) {
								vhVerifyStatus = verificationHistoryVertical.get(vhColumns).getText();
								assert1.assertEquals(vhVerifyStatus, "Pending at Bank");
							}
						}
				
				//Check Left accordian in verification history
				verificationHistoryVertical.get(0).click();
				Thread.sleep(2000);
                List<WebElement> LeftaccordianDetials=history.getVHLeftAccordian();
                for (int leftAcc = 0; leftAcc < LeftaccordianDetials.size(); leftAcc++) {
					List<WebElement> leftAccDetails=LeftaccordianDetials.get(leftAcc).findElements(By.tagName("div"));
					
					if (leftAcc==0) {
						String verifiedOn=leftAccDetails.get(2).getText();
						assert1.assertEquals(vhVerifyDate, verifiedOn);
					}else if(leftAcc==1) {
						String BankAccUpi=leftAccDetails.get(2).getText();
						assert1.assertEquals(accUpi, BankAccUpi);
					}else if(leftAcc==2) {
						String accIFSC=leftAccDetails.get(2).getText();
						assert1.assertEquals(ifsc, accIFSC);
					}else if(leftAcc==3) {
						String status=leftAccDetails.get(2).getText();
						assert1.assertEquals(vhVerifyStatus, status);
					}
					
				}
                
                //To check Right accordian in verification history
                List<WebElement> RightaccordianDetials=history.getVHRightAccordian();
                for (int rightAcc = 0; rightAcc < RightaccordianDetials.size(); rightAcc++) {
					List<WebElement> rightAccDetails=RightaccordianDetials.get(rightAcc).findElements(By.tagName("div"));
					
					if (rightAcc==0) {
						String Name=rightAccDetails.get(2).getText();
						assert1.assertEquals("-", Name);
					}else if(rightAcc==1) {
						String accVerifyId=rightAccDetails.get(2).getText();
						assert1.assertEquals(vhVerifyID, accVerifyId);
					}else if(rightAcc==2) {
						String accPaymentId=rightAccDetails.get(2).getText();
						assert1.assertEquals("-", accPaymentId);
					}else if(rightAcc==3) {
						String response=rightAccDetails.get(2).getText();
						assert1.assertEquals(response, "");
					}
				}
				}
				// checking the payment made is updated in payment history
				home.clickOnPaymentHistory();
				Thread.sleep(3000);
				assert1.assertEquals(home.getPageHeader(), "Payment History");

				List<WebElement> paymentHistory = history.getTxnEntries();
				for (int phRows = 0; phRows <=0; phRows++) {
					
						List<WebElement> paymentHistoryVertical = paymentHistory.get(phRows)
								.findElements(By.tagName("td"));
						for (int phColumns = 0; phColumns < paymentHistoryVertical.size(); phColumns++) {
							if (phColumns == 0) {
								String phPaymentDate = paymentHistoryVertical.get(phColumns).getText();
								assert1.assertNotEquals(vhVerifyDate, phPaymentDate);
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

					// fetching the details in verification details
					String verifyID = verify.getVerificationID();
					String paymentID = verify.getPaymentID();
					String accUpi = verify.getAccnoUpi();
					String ifsc = verify.getIfsc();
					String verifyDate = verify.getVerificationDate();
					String verifyName = verify.getNameInBank();
					String currentDate=java.getCurrentTime();
					assert1.assertTrue(verifyDate.contains(currentDate));

					// validating the details in verification details shown
					assert1.assertEquals(accUpi, excel.readDataFromExcel(sheetName, accRowNum, 1));
					assert1.assertEquals(ifsc, "-");
					assert1.assertEquals(paymentID, "-");
					assert1.assertNotEquals(verifyID, "-");

					// validating the verification response/status in verification details
					if (sheetName.equals("ValidDetails")) {
						assert1.assertTrue(verify.getValidVerificationStatus().isDisplayed());
						assert1.assertNotEquals(verifyName, "-");
					} else if (sheetName.equals("InvalidDetails")) {
						assert1.assertEquals(verify.getVerificationResponse(), "Invalid");
						assert1.assertEquals(verifyName, "-");
					}

					// write verifyID and payment ID into properties file
					property.writeToProperties("verifyID", verifyID, UtilitiesPath.PROPERTIES_PATH);
					property.writeToProperties("paymentID", paymentID, UtilitiesPath.PROPERTIES_PATH);
					System.out.println("Success: "+verifyID);

					// checking the verified bank detail is updated in verification history
					verify.clickOnGoToVerifyHistory();
					Thread.sleep(3000);
					assert1.assertEquals(home.getPageHeader(), "Verification History");

					List<WebElement> verificationHistory = history.getTxnEntries();
					String vhVerifyStatus=null;
					for (int vhRows = 0; vhRows <=0; vhRows++) {
						
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
								}if (vhColumns == 5) {
									String vhVerifyID = verificationHistoryVertical.get(vhColumns).getText();
									assert1.assertEquals(vhVerifyID, verifyID);
								} else if (vhColumns == 6) {
									vhVerifyStatus = verificationHistoryVertical.get(vhColumns).getText();

									if (sheetName.equals("ValidDetails")) {
										assert1.assertEquals(vhVerifyStatus, "Valid");
									} else if (sheetName.equals("InvalidDetails")) {
										assert1.assertEquals(vhVerifyStatus, "Invalid");
									}
								}
							}
						}
					
					//Check Left accordian in verification history
					verificationHistory.get(0).click();
					Thread.sleep(2000);
                    List<WebElement> LeftaccordianDetials=history.getVHLeftAccordian();
                    for (int leftAcc = 0; leftAcc < LeftaccordianDetials.size(); leftAcc++) {
						List<WebElement> leftAccDetails=LeftaccordianDetials.get(leftAcc).findElements(By.tagName("div"));
						
						if (leftAcc==0) {
							String verifiedOn=leftAccDetails.get(2).getText();
							assert1.assertEquals(verifyDate, verifiedOn);
						}else if(leftAcc==1) {
							String BankAccUpi=leftAccDetails.get(2).getText();
							assert1.assertEquals(accUpi, BankAccUpi);
						}else if(leftAcc==2) {
							String accIFSC=leftAccDetails.get(2).getText();
							assert1.assertEquals(ifsc, accIFSC);
						}else if(leftAcc==3) {
							String status=leftAccDetails.get(2).getText();
							assert1.assertEquals(vhVerifyStatus, status);
						}
					}
                    
                    //To check Right accordian in verification history
                    List<WebElement> RightaccordianDetials=history.getVHRightAccordian();
                    for (int rightAcc = 0; rightAcc < RightaccordianDetials.size(); rightAcc++) {
						List<WebElement> rightAccDetails=RightaccordianDetials.get(rightAcc).findElements(By.tagName("div"));
						
						if (rightAcc==0) {
							String Name=rightAccDetails.get(2).getText();
							assert1.assertEquals(verifyName, Name);
						}else if(rightAcc==1) {
							String accVerifyId=rightAccDetails.get(2).getText();
							assert1.assertEquals(verifyID, accVerifyId);
						}else if(rightAcc==2) {
							String accPaymentId=rightAccDetails.get(2).getText();
							assert1.assertEquals(paymentID, accPaymentId);
						}else if(rightAcc==3) {
							String response=rightAccDetails.get(2).getText();
							assert1.assertNotEquals(response, "null");
						}
					}

					// checking the payment made is updated in payment history
					home.clickOnPaymentHistory();
					Thread.sleep(3000);
					assert1.assertEquals(home.getPageHeader(), "Payment History");

					List<WebElement> paymentHistory = history.getTxnEntries();
					for (int phRows = 0; phRows <=0; phRows++) {
						if (phRows == 0) {
							List<WebElement> paymentHistoryVertical = paymentHistory.get(phRows)
									.findElements(By.tagName("td"));
							for (int phColumns = 0; phColumns < paymentHistoryVertical.size(); phColumns++) {
								if (phColumns == 2) {
									String phPaymentID = paymentHistoryVertical.get(phColumns).getText();
									assert1.assertNotEquals(paymentID, phPaymentID);
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

		freeUpiVerifyFlowScript("ValidDetails", 10);
	}

	@Test(priority = 1)
	public void validUpiVerification2() throws InterruptedException {

		freeUpiVerifyFlowScript("ValidDetails", 11);
	}

	// Invalid upi verification
	@Test(priority = 2)
	public void inValidUpiVerification1() throws InterruptedException {

		freeUpiVerifyFlowScript("InvalidDetails", 10);
	}

	@Test(priority = 3)
	public void inValidUpiVerification2() throws InterruptedException {

		freeUpiVerifyFlowScript("InvalidDetails", 11);
	}
	
	/*
	 *  To verify by setting the system config
	 */
	
//	Valid verifications
//	@Test(priority = 0)
//	public void validUpiVerification() throws InterruptedException {
//
//		DeleteBenePayload.deleteBenePayload(excel.readDataFromExcel("ValidDetails", 10, 1), "", "");
//		freeUpiVerifyFlowScript("ValidDetails", 10,false);
//	}
//	
//	@Test(priority = 1)
//	public void validUpiVerificationAtDB() throws InterruptedException {
//
//		freeUpiVerifyFlowScript("ValidDetails", 10,false);
//	}
//	
//	@Test(priority = 2)
//	public void validUpiVerificationWithImpsDown() throws InterruptedException {
//
//		DeleteBenePayload.deleteBenePayload(excel.readDataFromExcel("ValidDetails", 10, 1), "", "");
//		freeUpiVerifyFlowScript("ValidDetails", 10,true);
//	}
//	
////	Invalid verifications
//	@Test(priority = 3)
//	public void inValidUpiVerification() throws InterruptedException {
//
//		DeleteBenePayload.deleteBenePayload(excel.readDataFromExcel("InvalidDetails", 10, 1), "", "");
//		freeUpiVerifyFlowScript("InvalidDetails", 10,false);
//	}
//	
//	@Test(priority = 4)
//	public void inValidUpiVerificationAtDB() throws InterruptedException {
//
//		DeleteBenePayload.deleteBenePayload(excel.readDataFromExcel("InvalidDetails", 10, 1), "", "");
//		freeUpiVerifyFlowScript("InvalidDetails", 10,false);
//	}
//	
//	@Test(priority = 4)
//	public void inValidUpiVerificationImpsDown() throws InterruptedException {
//
//		DeleteBenePayload.deleteBenePayload(excel.readDataFromExcel("InvalidDetails", 10, 1), "", "");
//		freeUpiVerifyFlowScript("InvalidDetails", 10,true);
//	}
}
