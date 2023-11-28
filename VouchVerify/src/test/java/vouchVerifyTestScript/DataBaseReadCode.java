package vouchVerifyTestScript;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.annotations.Test;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;
import io.restassured.response.Response;
import payload.VerificationStatusCheckPayload;

public class DataBaseReadCode {

	//@Test
	public void fetchWeebhookFromDB() throws SQLException {

		// creating the instance to property and excel classes and initializing it
		PropertiesUtility property = new PropertiesUtility();
		ExcelUtility excel = new ExcelUtility();
		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);

		// JDBC URL, username, and password of Oracle database
		String url = "jdbc:oracle:thin:@escrow-read-replica.cxbs4z99zd4t.ap-south-1.rds.amazonaws.com:1521:ORCL";
		String user = "reports";
		String password = "a0242ac120002";
		Connection connection = null;
		try {
			// Register the Oracle JDBC driver
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// Create a connection to the database
			connection = DriverManager.getConnection(url, user, password);

			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;

			// from 79 need to fetch so j=76
			for (int j = 1; j <= excel.getLastRowNum("verifyRef"); j++) {

				try {
					String sqlQuery = "SELECT * FROM prod.EL_OUTWARD_WEBHOOKS "
							+ "WHERE ledger_label LIKE '%GuruFin%' AND payload LIKE '%"
							+ excel.readDataFromExcel("verifyRef", j, 0) + "\"%'";

					// Perform database operations here
					preparedStatement = connection.prepareStatement(sqlQuery);

					// Execute the query
					resultSet = preparedStatement.executeQuery();
					// Process the results
					int count = 1;
					while (resultSet.next()) {
						if (count == 1) {
							// Access the result set data, e.g., resultSet.getString("column_name")
							// Replace "column_name" with the actual column names in your table

							// updating the laderlabel
							excel.writeToExcel("verifyRef", j, 5, resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
							// updating the payload
							excel.writeToExcel("verifyRef", j, 4, resultSet.getString(3), UtilitiesPath.EXCEL_PATH);
							// updating the status
							excel.writeToExcel("verifyRef", j, 3, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
							// updating the timeStamp
							excel.writeToExcel("verifyRef", j, 2, resultSet.getString(5), UtilitiesPath.EXCEL_PATH);
							count++;
						} else if (count == 2) {
							// updating the laderlabel
							excel.writeToExcel("verifyRef", j, 9, resultSet.getString(2), UtilitiesPath.EXCEL_PATH);
							// updating the payload
							excel.writeToExcel("verifyRef", j, 8, resultSet.getString(3), UtilitiesPath.EXCEL_PATH);
							// updating the status
							excel.writeToExcel("verifyRef", j, 7, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
							// updating the timeStamp
							excel.writeToExcel("verifyRef", j, 6, resultSet.getString(5), UtilitiesPath.EXCEL_PATH);
							count++;
						}

					}
				} finally {
					System.out.println(j + ": done");
					resultSet.close();
					preparedStatement.close();
				}
				excel.closeExcel();
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the connection
			connection.close();
		}
	}

	//@Test
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
				} else if(excel.readDataFromExcel("Sheet1", j, 3).contains(verifyRef) && count==3) {
					System.out.println(verifyRef);
				}

			}
          System.out.println(verifyRef+ " : done");
		}

        excel.closeExcel();
	}

}
