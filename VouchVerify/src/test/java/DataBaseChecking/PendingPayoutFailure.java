package DataBaseChecking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbcp2.BasicDataSource;
import org.testng.annotations.Test;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;

public class PendingPayoutFailure {

	@Test
	public void pendingPayoutFailureDetailsFetchInDB() {

		PropertiesUtility property = null;
		ExcelUtility excel = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// creating the instance to property and excel classes and initializing it
			property = new PropertiesUtility();
			excel = new ExcelUtility();
			property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
			excel.excelInit(UtilitiesPath.EXCEL_PATH);

			// JDBC URL, username, and password of Oracle database
			String url = "jdbc:oracle:thin:@escrowprod.cxbs4z99zd4t.ap-south-1.rds.amazonaws.com:1521:ORCL";
			String user = "REPORTS";
			String password = "F3PTZ!gZmLM^";

			// Create a connection pool
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setUrl(url);
			dataSource.setUsername(user);
			dataSource.setPassword(password);
			dataSource.setMinIdle(5);
			dataSource.setMaxIdle(10);
			dataSource.setMaxOpenPreparedStatements(100);

			// Register the Oracle JDBC driver
			// Class.forName("oracle.jdbc.driver.OracleDriver");

			// from 79 need to fetch so j=76
			for (int j = 1; j <=excel.getLastRowNum("verifyRef1") ; j++) {
                
				try {
					// Checking the icici statement
					String iciciQuery = "select * from prod.bp_icici_statement where remarks like '%"
							+ excel.readDataFromExcel("verifyRef1", j, 0) + "%'";

					// Create a connection to the database
					connection = dataSource.getConnection();

					// Perform database operations here
					preparedStatement = connection.prepareStatement(iciciQuery);

					// Execute the query
					resultSet = preparedStatement.executeQuery();

					// checking the number of rows
					int iciciNoRows = 0;
					
					//Transaction id
					String iciciCreditTxnID=null;
					String iciciDebitTxnID=null;
					
					//Txn type
					String creditTxnType="null";
					String debitTxnType="null";

					while (resultSet.next()) {
						if (resultSet.getString(6).equals("CR")) {
							iciciCreditTxnID=resultSet.getString(3);
							creditTxnType=resultSet.getString(6);
						}else if(resultSet.getString(6).equals("DR")) {
							iciciDebitTxnID=resultSet.getString(3);
							debitTxnType=resultSet.getString(6);
						}
						iciciNoRows++;
					}

//					Consolidation statement check when two icici statement are there
					if(iciciNoRows==2&&creditTxnType.equals("CR")&&debitTxnType.equals("DR")) {
						//Checking the consolidation statement
						resultSet.close();
						preparedStatement.close();

						// Checking the consolidated statement
						String consQuery = "select * from prod.CONSOLIDATED_BANK_STATEMENT where remarks like '%"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "%'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(consQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						// checking the number of rows
						int consNoRows = 0;
						
						//Transaction id
						String consCreditTxnID=null;
						String consDebitTxnID=null;

						//Remark
						String remark="";
						
						//Txn date
						String creditTxnDate=null;
						String debitTxnDate=null;
						
						//Txn type
						String consCreditTxnType=null;
						String consDebitTxnType=null;
						while (resultSet.next()) {
							if (resultSet.getString(6).equals("CR")) {
								//fetching txn id
								consCreditTxnID=resultSet.getString(3);
								//fetching remark
								remark+=resultSet.getString(9);
								//fetching txn date
								creditTxnDate=resultSet.getString(4);
								consCreditTxnType=resultSet.getString(6);
							}else if(resultSet.getString(6).equals("DR")) {
								//fetching txn id
								consDebitTxnID=resultSet.getString(3);
								//fetching remark
								remark+=resultSet.getString(9);
								//fetching txn date
								debitTxnDate=resultSet.getString(4);
								consDebitTxnType=resultSet.getString(6);
							}
							consNoRows++;
						}
						
						if(consNoRows==2&&consCreditTxnType.equals("CR")&&consDebitTxnType.equals("DR")) {
							
							 SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						        Date creditDate = dateFormat.parse(creditTxnDate);
						        Date DebitDate = dateFormat.parse(debitTxnDate);
						        
						        //checking the first which txn happened 
						        if(creditDate.before(DebitDate)) {
						        	excel.writeToExcel("delay", j, 6, "First Credit happened", UtilitiesPath.EXCEL_PATH);
						        }
						        
						        //writing remark
						        excel.writeToExcel("delay", j, 4, remark, UtilitiesPath.EXCEL_PATH);
						        
						      //Checking the new payout log
								resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String newPayoutQuery = "select * from prod.payout_log_view where bankref = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(newPayoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int oldPayoutNoRows=0;
								
								while(resultSet.next()) {
								  //writing status into excel
								  excel.writeToExcel("delay", j, 1, resultSet.getString(7), UtilitiesPath.EXCEL_PATH);
								  //writing ledger into excel
								  excel.writeToExcel("delay", j,0 , resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
								  oldPayoutNoRows++;
								}
								if(oldPayoutNoRows>1) {
								  excel.writeToExcel("delay", j, 1, "More than 1", UtilitiesPath.EXCEL_PATH);
								}
								
								//Checking the backup payout log
								resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String backupPayoutQuery = "select * from prod.backup_bl_icici_payout_log where bankref = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(backupPayoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int backupPayoutNoRows=0;
								
								while(resultSet.next()) {
								  //writing status into excel
								  excel.writeToExcel("delay", j, 1, resultSet.getString(7), UtilitiesPath.EXCEL_PATH);
								  //writing ledger into excel
								  excel.writeToExcel("delay", j,0 , resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
								  backupPayoutNoRows++;
								}
								if(backupPayoutNoRows>1) {
								  excel.writeToExcel("delay", j, 1, "More than 1", UtilitiesPath.EXCEL_PATH);
								}
						        if(oldPayoutNoRows==0&&backupPayoutNoRows==0) {
						          excel.writeToExcel("delay", j, 1, "Unkown", UtilitiesPath.EXCEL_PATH);
						          excel.writeToExcel("delay", j, 0, "Unkown", UtilitiesPath.EXCEL_PATH);
						        }
						        
						        //Checking reversal ledger entries
						        resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String ledgerQuery = "select * from prod.ledger_entries where collect_bank_rrn = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(ledgerQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int collectLedgerCount=0;
								while(resultSet.next()) {
									
									if(consCreditTxnID.equals(resultSet.getString(11))&&resultSet.getString(17).equals("Cr")){
										excel.writeToExcel("delay", j, 3, "Yes", UtilitiesPath.EXCEL_PATH);
									}
									collectLedgerCount++;
								}
								if(collectLedgerCount==0) {
									excel.writeToExcel("delay", j, 3, "No", UtilitiesPath.EXCEL_PATH);
								}
								
								 //Checking payout ledger entries
						        resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String payoutQuery = "select * from prod.ledger_entries where payout_bank_rrn = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(payoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int payoutLedgerCount=0;
								while(resultSet.next()) {
									
									if(consDebitTxnID.equals(resultSet.getString(11))&&resultSet.getString(17).equals("Dr")){
										excel.writeToExcel("delay", j, 2, "Yes", UtilitiesPath.EXCEL_PATH);
									}
									payoutLedgerCount++;
								}
								if(payoutLedgerCount==0) {
									excel.writeToExcel("delay", j, 2, "No", UtilitiesPath.EXCEL_PATH);
								}  
						}else if(consNoRows>2) {
							excel.writeToExcel("delay", j, 5, "More than 2 consalidation statment", UtilitiesPath.EXCEL_PATH);
						}else if(consNoRows==0) {
							excel.writeToExcel("delay", j, 5, "0 consalidation statment", UtilitiesPath.EXCEL_PATH);
						}else {
							excel.writeToExcel("delay", j, 5, "Check Manually", UtilitiesPath.EXCEL_PATH);
						}
					}else if (iciciNoRows==1&&creditTxnType.equals("null")&&debitTxnType.equals("DR")) {
						
						//Checking the consolidation statement
						resultSet.close();
						preparedStatement.close();

						// Checking the consolidated statement
						String consQuery = "select * from prod.CONSOLIDATED_BANK_STATEMENT where remarks like '%"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "%'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(consQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						// checking the number of rows
						int consNoRows = 0;
						
						//Transaction id
						String consCreditTxnID=null;
						String consDebitTxnID=null;

						//Remark
						String remark="";
						
						//Txn date
						String creditTxnDate=null;
						String debitTxnDate=null;
						
						//Txn type
						String consCreditTxnType=null;
						String consDebitTxnType=null;
						while (resultSet.next()) {
							if (resultSet.getString(6).equals("CR")) {
								//fetching txn id
								consCreditTxnID=resultSet.getString(3);
								//fetching remark
								remark+=resultSet.getString(9);
								//fetching txn date
								creditTxnDate=resultSet.getString(4);
								consCreditTxnType=resultSet.getString(6);
							}else if(resultSet.getString(6).equals("DR")) {
								//fetching txn id
								consDebitTxnID=resultSet.getString(3);
								//fetching remark
								remark+=resultSet.getString(9);
								//fetching txn date
								debitTxnDate=resultSet.getString(4);
								consDebitTxnType=resultSet.getString(6);
							}
							consNoRows++;
						}

						if(consNoRows==2&&consCreditTxnType.equals("CR")&&consDebitTxnType.equals("DR")) {
							
							excel.writeToExcel("delay", j, 5, "Found 1dr in icici & 1cr 1dr in cons", UtilitiesPath.EXCEL_PATH);
							 SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						        Date creditDate = dateFormat.parse(creditTxnDate);
						        Date DebitDate = dateFormat.parse(debitTxnDate);
						        
						        //checking the first which txn happened 
						        if(creditDate.before(DebitDate)) {
						        	excel.writeToExcel("delay", j, 6, "First Credit happened", UtilitiesPath.EXCEL_PATH);
						        }
						        
						        //writing remark
						        excel.writeToExcel("delay", j, 4, remark, UtilitiesPath.EXCEL_PATH);
						        
						      //Checking the new payout log
								resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String newPayoutQuery = "select * from prod.payout_log_view where bankref = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(newPayoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int oldPayoutNoRows=0;
								
								while(resultSet.next()) {
								  //writing status into excel
								  excel.writeToExcel("delay", j, 1, resultSet.getString(7), UtilitiesPath.EXCEL_PATH);
								  //writing ledger into excel
								  excel.writeToExcel("delay", j,0 , resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
								  oldPayoutNoRows++;
								}
								if(oldPayoutNoRows>1) {
								  excel.writeToExcel("delay", j, 1, "More than 1", UtilitiesPath.EXCEL_PATH);
								}
								
								//Checking the backup payout log
								resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String backupPayoutQuery = "select * from prod.backup_bl_icici_payout_log where bankref = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(backupPayoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int backupPayoutNoRows=0;
								
								while(resultSet.next()) {
								  //writing status into excel
								  excel.writeToExcel("delay", j, 1, resultSet.getString(7), UtilitiesPath.EXCEL_PATH);
								  //writing ledger into excel
								  excel.writeToExcel("delay", j,0 , resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
								  backupPayoutNoRows++;
								}
								if(backupPayoutNoRows>1) {
								  excel.writeToExcel("delay", j, 1, "More than 1", UtilitiesPath.EXCEL_PATH);
								}
						        if(oldPayoutNoRows==0&&backupPayoutNoRows==0) {
						          excel.writeToExcel("delay", j, 1, "Unkown", UtilitiesPath.EXCEL_PATH);
						          excel.writeToExcel("delay", j, 0, "Unkown", UtilitiesPath.EXCEL_PATH);
						        }
						        
						        //Checking reversal ledger entries
						        resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String ledgerQuery = "select * from prod.ledger_entries where collect_bank_rrn = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(ledgerQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int collectLedgerCount=0;
								while(resultSet.next()) {
									
									if(consCreditTxnID.equals(resultSet.getString(11))&&resultSet.getString(17).equals("Cr")){
										excel.writeToExcel("delay", j, 3, "Yes", UtilitiesPath.EXCEL_PATH);
									}
									collectLedgerCount++;
								}
								if(collectLedgerCount==0) {
									excel.writeToExcel("delay", j, 3, "No", UtilitiesPath.EXCEL_PATH);
								}
								
								 //Checking payout ledger entries
						        resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String payoutQuery = "select * from prod.ledger_entries where payout_bank_rrn = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(payoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int payoutLedgerCount=0;
								while(resultSet.next()) {
									
									if(consDebitTxnID.equals(resultSet.getString(11))&&resultSet.getString(17).equals("Dr")){
										excel.writeToExcel("delay", j, 2, "Yes", UtilitiesPath.EXCEL_PATH);
									}
									payoutLedgerCount++;
								}
								if(payoutLedgerCount==0) {
									excel.writeToExcel("delay", j, 2, "No", UtilitiesPath.EXCEL_PATH);
								}  
						}else if(consNoRows>2) {
							excel.writeToExcel("delay", j, 5, "More than 2 consalidation statment", UtilitiesPath.EXCEL_PATH);
						}else if(consNoRows==0) {
							excel.writeToExcel("delay", j, 5, "0 consalidation statment", UtilitiesPath.EXCEL_PATH);
						}else {
							excel.writeToExcel("delay", j, 5, "Check Manually", UtilitiesPath.EXCEL_PATH);
						}
					}else if(iciciNoRows==0) {
						excel.writeToExcel("delay", j, 5, "0 icici statment", UtilitiesPath.EXCEL_PATH);
					}else if(iciciNoRows>2) {
						excel.writeToExcel("delay", j, 5, "More than 2 icici statment", UtilitiesPath.EXCEL_PATH);
					}else if(iciciNoRows==2&&creditTxnType.equals("null")&&debitTxnType.equals("DR")) {
						//Checking the consolidation statement
						resultSet.close();
						preparedStatement.close();

						// Checking the consolidated statement
						String consQuery = "select * from prod.CONSOLIDATED_BANK_STATEMENT where remarks like '%"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "%'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(consQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						// checking the number of rows
						int consNoRows = 0;
						
						//Transaction id
						String consCreditTxnID=null;
						String consDebitTxnID=null;

						//Remark
						String remark="";
						
						//Txn date
						String creditTxnDate=null;
						String debitTxnDate=null;
						
						//Txn type
						String consCreditTxnType=null;
						String consDebitTxnType=null;
						while (resultSet.next()) {
							if (resultSet.getString(6).equals("CR")) {
								//fetching txn id
								consCreditTxnID=resultSet.getString(3);
								//fetching remark
								remark+=resultSet.getString(9);
								//fetching txn date
								creditTxnDate=resultSet.getString(4);
								consCreditTxnType=resultSet.getString(6);
							}else if(resultSet.getString(6).equals("DR")) {
								//fetching txn id
								consDebitTxnID=resultSet.getString(3);
								//fetching remark
								remark+=resultSet.getString(9);
								//fetching txn date
								debitTxnDate=resultSet.getString(4);
								consDebitTxnType=resultSet.getString(6);
							}
							consNoRows++;
						}
						
						if(consNoRows==2&&consCreditTxnType.equals("CR")&&consDebitTxnType.equals("DR")) {
							
							excel.writeToExcel("delay", j, 5, "2dr at Icici and 1cr 1dr at cons", UtilitiesPath.EXCEL_PATH);
							
							 SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						        Date creditDate = dateFormat.parse(creditTxnDate);
						        Date DebitDate = dateFormat.parse(debitTxnDate);
						        
						        //checking the first which txn happened 
						        if(creditDate.before(DebitDate)) {
						        	excel.writeToExcel("delay", j, 6, "First Credit happened", UtilitiesPath.EXCEL_PATH);
						        }
						        
						        //writing remark
						        excel.writeToExcel("delay", j, 4, remark, UtilitiesPath.EXCEL_PATH);
						        
						      //Checking the new payout log
								resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String newPayoutQuery = "select * from prod.payout_log_view where bankref = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(newPayoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int oldPayoutNoRows=0;
								
								while(resultSet.next()) {
								  //writing status into excel
								  excel.writeToExcel("delay", j, 1, resultSet.getString(7), UtilitiesPath.EXCEL_PATH);
								  //writing ledger into excel
								  excel.writeToExcel("delay", j,0 , resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
								  oldPayoutNoRows++;
								}
								if(oldPayoutNoRows>1) {
								  excel.writeToExcel("delay", j, 1, "More than 1", UtilitiesPath.EXCEL_PATH);
								}
								
								//Checking the backup payout log
								resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String backupPayoutQuery = "select * from prod.backup_bl_icici_payout_log where bankref = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(backupPayoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int backupPayoutNoRows=0;
								
								while(resultSet.next()) {
								  //writing status into excel
								  excel.writeToExcel("delay", j, 1, resultSet.getString(7), UtilitiesPath.EXCEL_PATH);
								  //writing ledger into excel
								  excel.writeToExcel("delay", j,0 , resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
								  backupPayoutNoRows++;
								}
								if(backupPayoutNoRows>1) {
								  excel.writeToExcel("delay", j, 1, "More than 1", UtilitiesPath.EXCEL_PATH);
								}
						        if(oldPayoutNoRows==0&&backupPayoutNoRows==0) {
						          excel.writeToExcel("delay", j, 1, "Unkown", UtilitiesPath.EXCEL_PATH);
						          excel.writeToExcel("delay", j, 0, "Unkown", UtilitiesPath.EXCEL_PATH);
						        }
						        
						        //Checking reversal ledger entries
						        resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String ledgerQuery = "select * from prod.ledger_entries where collect_bank_rrn = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(ledgerQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int collectLedgerCount=0;
								while(resultSet.next()) {
									
									if(consCreditTxnID.equals(resultSet.getString(11))&&resultSet.getString(17).equals("Cr")){
										excel.writeToExcel("delay", j, 3, "Yes", UtilitiesPath.EXCEL_PATH);
									}
									collectLedgerCount++;
								}
								if(collectLedgerCount==0) {
									excel.writeToExcel("delay", j, 3, "No", UtilitiesPath.EXCEL_PATH);
								}
								
								 //Checking payout ledger entries
						        resultSet.close();
								preparedStatement.close();

								// Checking the consolidated statement
								String payoutQuery = "select * from prod.ledger_entries where payout_bank_rrn = '"
										+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";
								
								// Perform database operations here
								preparedStatement = connection.prepareStatement(payoutQuery);

								// Execute the query
								resultSet = preparedStatement.executeQuery();
								
								int payoutLedgerCount=0;
								while(resultSet.next()) {
									
									if(consDebitTxnID.equals(resultSet.getString(11))&&resultSet.getString(17).equals("Dr")){
										excel.writeToExcel("delay", j, 2, "Yes", UtilitiesPath.EXCEL_PATH);
									}
									payoutLedgerCount++;
								}
								if(payoutLedgerCount==0) {
									excel.writeToExcel("delay", j, 2, "No", UtilitiesPath.EXCEL_PATH);
								}  
						}else if(consNoRows>2) {
							excel.writeToExcel("delay", j, 5, "More than 2 consalidation statment", UtilitiesPath.EXCEL_PATH);
						}else if(consNoRows==0) {
							excel.writeToExcel("delay", j, 5, "0 consalidation statment", UtilitiesPath.EXCEL_PATH);
						}else {
							excel.writeToExcel("delay", j, 5, "Check Manually", UtilitiesPath.EXCEL_PATH);
						}
					}else {
						excel.writeToExcel("delay", j, 5, "Check Manually icici condition not matching", UtilitiesPath.EXCEL_PATH);
					}
					System.out.println(j +": done");

				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					resultSet.close();
					preparedStatement.close();
					connection.close();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			// Close the connection
			excel.closeExcel();
		}
	}
}
