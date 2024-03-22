package vouchVerifyTestScript;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbcp2.BasicDataSource;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;

public class DataBaseReadCode {

	// @Test
	public void checkIciciConsLedgerWebhookInDB() throws SQLException {

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
			for (int j = 137; j < excel.getLastRowNum("verifyRef1"); j++) {

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

					// icic txn type
					String iciciTxnType = null;
					String iciciTxn2Type = null;
					String iciciTxn3Type = null;

					// icici txn id
					String iciciTxnID = null;
					while (resultSet.next()) {
						if (iciciNoRows == 0) {
							iciciTxnType = resultSet.getString(6);
							iciciTxnID = resultSet.getString(3);
						} else if (iciciNoRows == 1) {
							iciciTxn2Type = resultSet.getString(6);
						} else if (iciciNoRows == 2) {
							iciciTxn3Type = resultSet.getString(6);
						}
						iciciNoRows++;
					}

					if (iciciNoRows == 1 && iciciTxnType.equals("CR")) {
						resultSet.close();
						preparedStatement.close();

						// Checking the consolidated statement
						String consQuery = "select * from prod.CONSOLIDATED_BANK_STATEMENT where remarks like '%"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "%'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(consQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						int consNoRows = 0;

						String consTxnID = null;
						while (resultSet.next()) {

							if (consNoRows == 0 && resultSet.getString(6).equals("CR") && iciciNoRows == 1) {

								// updating the CONSOLIDATE AND API STATMENT
								excel.writeToExcel("delay", j, 2, "1CR", UtilitiesPath.EXCEL_PATH);
//								// updating the txn date
//								excel.writeToExcel("delay", j, 2, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
//								// updating the remark
//								excel.writeToExcel("delay", j, 6, resultSet.getString(9), UtilitiesPath.EXCEL_PATH);

								// get txn id
								if (iciciTxnID.contains(resultSet.getString(3))) {
									// updating the txn id
									excel.writeToExcel("delay", j, 4, resultSet.getString(3), UtilitiesPath.EXCEL_PATH);
								} else {
									// updating the txn id
									excel.writeToExcel("delay", j, 4, "Txn ID mis-match", UtilitiesPath.EXCEL_PATH);
								}

							}
							consNoRows++;
						}
						if (consNoRows == 2) {
							excel.writeToExcel("delay", j, 2, "Two Txn in cons", UtilitiesPath.EXCEL_PATH);
						}
						if (consNoRows == 0) {
							excel.writeToExcel("delay", j, 2, "Txn not found in cons", UtilitiesPath.EXCEL_PATH);
						}

						resultSet.close();
						preparedStatement.close();

						// Checking the ledger statement
						String ledgerQuery = "select * from prod.ledger_entries where collect_bank_rrn = '"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(ledgerQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						int ledgerNoRows = 0;

						while (resultSet.next()) {
							if (consNoRows == 1 && iciciTxnID.contains(resultSet.getString(11))) {

								// updating the ledger label
								excel.writeToExcel("delay", j, 1, "Yes", UtilitiesPath.EXCEL_PATH);
//								// updating the ledger passed on date
//								excel.writeToExcel("delay", j, 3, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
//								// updating the sender vpa
//								excel.writeToExcel("delay", j, 4, resultSet.getString(8), UtilitiesPath.EXCEL_PATH);

							}
							ledgerNoRows++;
						}
						if (ledgerNoRows > 1) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 4, "More than 1 ledger found", UtilitiesPath.EXCEL_PATH);
						}
						if (ledgerNoRows == 0) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 1, "No", UtilitiesPath.EXCEL_PATH);
						}

//						webhook
						resultSet.close();
						preparedStatement.close();

						// Checking the ledger statement
						String webhookQuery = "select * from prod.timepay_webhooks where rrn = '"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(webhookQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						int webhookNoRows = 0;

						// String ledgerTxnID = null;
						while (resultSet.next()) {

							if (consNoRows == 1 && iciciTxnID.contains(resultSet.getString(2))) {

								// updating the ledger label
								excel.writeToExcel("delay", j, 0, "Yes", UtilitiesPath.EXCEL_PATH);
//								// updating the ledger passed on date
//								excel.writeToExcel("delay", j, 3, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
//								// updating the sender vpa
//								excel.writeToExcel("delay", j, 4, resultSet.getString(8), UtilitiesPath.EXCEL_PATH);

							}
							webhookNoRows++;
						}
						if (webhookNoRows > 1) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 0, "More than 1 webhook found", UtilitiesPath.EXCEL_PATH);
						}
						if (webhookNoRows == 0) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 0, "No", UtilitiesPath.EXCEL_PATH);
						}
					} else if (iciciNoRows == 2 && iciciTxnType.equals("CR") && iciciTxn2Type.equals("CR")) {
						resultSet.close();
						preparedStatement.close();

						excel.writeToExcel("delay", j, 3, "2 icici statment", UtilitiesPath.EXCEL_PATH);

						// Checking the consolidated statement
						String consQuery = "select * from prod.CONSOLIDATED_BANK_STATEMENT where remarks like '%"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "%'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(consQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						int consNoRows = 0;

						String consTxnID = null;
						while (resultSet.next()) {

							if (consNoRows == 0) {

								// updating the amount
								excel.writeToExcel("delay", j, 2, "1CR", UtilitiesPath.EXCEL_PATH);
								// updating the txn date
//								excel.writeToExcel("delay", j, 2, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
//								// updating the remark
//								excel.writeToExcel("delay", j, 6, resultSet.getString(9), UtilitiesPath.EXCEL_PATH);

								// get txn id
								if (iciciTxnID.contains(resultSet.getString(3))) {
									// updating the txn id
									excel.writeToExcel("delay", j, 4, resultSet.getString(3), UtilitiesPath.EXCEL_PATH);
								} else {
									// updating the txn id
									excel.writeToExcel("delay", j, 4, "Txn ID mis-match", UtilitiesPath.EXCEL_PATH);
								}

							}
							consNoRows++;
						}
						if (consNoRows == 2) {
							excel.writeToExcel("delay", j, 2, "Two Txn in cons", UtilitiesPath.EXCEL_PATH);
						}
						if (consNoRows == 0) {
							excel.writeToExcel("delay", j, 2, "Txn not found in cons", UtilitiesPath.EXCEL_PATH);
						}

						resultSet.close();
						preparedStatement.close();

						// Checking the ledger statement
						String ledgerQuery = "select * from prod.ledger_entries where collect_bank_rrn = '"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(ledgerQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						int ledgerNoRows = 0;

						String ledgerTxnID = null;
						while (resultSet.next()) {

							if (consNoRows == 1 && iciciTxnID.contains(resultSet.getString(11))) {

								// updating the ledger label
								excel.writeToExcel("delay", j, 1, "Yes", UtilitiesPath.EXCEL_PATH);
//								// updating the ledger passed on date
//								excel.writeToExcel("delay", j, 3, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
//								// updating the sender vpa
//								excel.writeToExcel("delay", j, 4, resultSet.getString(8), UtilitiesPath.EXCEL_PATH);

							}
							ledgerNoRows++;
						}
						if (ledgerNoRows > 1) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 1, "More than 1 ledger found", UtilitiesPath.EXCEL_PATH);
						}
						if (ledgerNoRows == 0) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 1, "No", UtilitiesPath.EXCEL_PATH);
						}

//						webhook
						resultSet.close();
						preparedStatement.close();

						// Checking the ledger statement
						String webhookQuery = "select * from prod.timepay_webhooks where rrn = '"
								+ excel.readDataFromExcel("verifyRef1", j, 0) + "'";

						// Perform database operations here
						preparedStatement = connection.prepareStatement(webhookQuery);

						// Execute the query
						resultSet = preparedStatement.executeQuery();

						int webhookNoRows = 0;

						// String ledgerTxnID = null;
						while (resultSet.next()) {

							if (consNoRows == 1 && iciciTxnID.contains(resultSet.getString(2))) {

								// updating the ledger label
								excel.writeToExcel("delay", j, 0, "Yes", UtilitiesPath.EXCEL_PATH);
//								// updating the ledger passed on date
//								excel.writeToExcel("delay", j, 3, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
//								// updating the sender vpa
//								excel.writeToExcel("delay", j, 4, resultSet.getString(8), UtilitiesPath.EXCEL_PATH);

							}
							webhookNoRows++;
						}
						if (webhookNoRows > 1) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 0, "More than 1 webhook found", UtilitiesPath.EXCEL_PATH);
						}
						if (webhookNoRows == 0) {
							// updating the ledger label
							excel.writeToExcel("delay", j, 0, "No", UtilitiesPath.EXCEL_PATH);
						}

					} else if (iciciNoRows == 2 && ((iciciTxnType.equals("DR") && iciciTxn2Type.equals("CR"))
							|| (iciciTxn2Type.equals("DR") && iciciTxnType.equals("CR")))) {
						// updating remark
						excel.writeToExcel("delay", j, 2, "1CR & 1DR", UtilitiesPath.EXCEL_PATH);
						excel.writeToExcel("delay", j, 4, "1CR & 1DR", UtilitiesPath.EXCEL_PATH);
					} else if (iciciNoRows > 2) {
						// updating remark
						excel.writeToExcel("delay", j, 2, "More than 2 txn", UtilitiesPath.EXCEL_PATH);
					}
					System.out.println(j + ": done");
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

	

	// @Test
	public void toAlterTheWebhooksWithVerify() {

		// creating the instance to property and excel classes and initializing it
		PropertiesUtility property = new PropertiesUtility();
		ExcelUtility excel = new ExcelUtility();
		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);

		for (int i = 1; i <= excel.getLastRowNum("verifyRef"); i++) {

			String verifyRef = excel.readDataFromExcel("verifyRef", i, 0) + "\"";
			int count = 1;
			for (int j = 1; j <= excel.getLastRowNum("Sheet1"); j++) {

				if (excel.readDataFromExcel("Sheet1", j, 3).contains(verifyRef) && count == 1) {

					// updating the timeStamp
					String timeStamp = excel.readDataFromExcel("Sheet1", j, 6);
					excel.writeToExcel("verifyRef", i, 2, timeStamp, UtilitiesPath.EXCEL_PATH);

					// updating the status
					String status = excel.readDataFromExcel("Sheet1", j, 5);
					excel.writeToExcel("verifyRef", i, 3, status, UtilitiesPath.EXCEL_PATH);

					// updating the payload
					String payload = excel.readDataFromExcel("Sheet1", j, 3);
					excel.writeToExcel("verifyRef", i, 4, payload, UtilitiesPath.EXCEL_PATH);

					// updating the ladger label
					String laderLabel = excel.readDataFromExcel("Sheet1", j, 1);
					excel.writeToExcel("verifyRef", i, 5, laderLabel, UtilitiesPath.EXCEL_PATH);
					count++;
					continue;
				} else if (excel.readDataFromExcel("Sheet1", j, 3).contains(verifyRef) && count == 2) {
					// updating the timeStamp
					String timeStamp = excel.readDataFromExcel("Sheet1", j, 6);
					excel.writeToExcel("verifyRef", i, 6, timeStamp, UtilitiesPath.EXCEL_PATH);

					// updating the status
					String status = excel.readDataFromExcel("Sheet1", j, 5);
					excel.writeToExcel("verifyRef", i, 7, status, UtilitiesPath.EXCEL_PATH);

					// updating the payload
					String payload = excel.readDataFromExcel("Sheet1", j, 3);
					excel.writeToExcel("verifyRef", i, 8, payload, UtilitiesPath.EXCEL_PATH);

					// updating the ladger label
					String laderLabel = excel.readDataFromExcel("Sheet1", j, 1);
					excel.writeToExcel("verifyRef", i, 9, laderLabel, UtilitiesPath.EXCEL_PATH);
					count++;
					continue;
				} else if (excel.readDataFromExcel("Sheet1", j, 3).contains(verifyRef) && count == 3) {
					System.out.println(verifyRef);
				}

			}
			System.out.println(verifyRef + " : done");
		}

		excel.closeExcel();
	}

}
