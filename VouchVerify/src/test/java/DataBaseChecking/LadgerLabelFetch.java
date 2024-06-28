package DataBaseChecking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbcp2.BasicDataSource;
import org.testng.annotations.Test;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;

public class LadgerLabelFetch {

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

			for (int j = 236; j <= excel.getLastRowNum("utrtxn"); j++) {

				if(excel.readDataFromExcel("utrtxn", j, 0).equals("")) {
					break;
				}
				try {
					String ladgerQuery="select * from prod.backup_bl_icici_payout_log where payout_ref= '"+excel.readDataFromExcel("utrtxn", j, 0)+"'";
					//System.out.println(ladgerQuery);

					// collect query
					// select * from prod.ledger_entries where collect_bank_rrn = '312197940499' and
					// bank_statement_tran_id = 'C44890304';

					// payout query
					// select * from prod.ledger_entries where payout_bank_rrn = '313702070111' and
					// bank_statement_tran_id = 'C44890304';

					// Create a connection to the database
					connection = dataSource.getConnection();

					// Perform database operations here
					preparedStatement = connection.prepareStatement(ladgerQuery);

					// Execute the query
					resultSet = preparedStatement.executeQuery();
					
					// checking the number of rows
					int ladgerRows = 0;
					while(resultSet.next()) {
						
						//Bankref
						excel.writeToExcel("utrtxn", j, 1, resultSet.getString(4), UtilitiesPath.EXCEL_PATH);
						//Acc No
						excel.writeToExcel("utrtxn", j, 2, resultSet.getString(10), UtilitiesPath.EXCEL_PATH);
						//Ifsc
						excel.writeToExcel("utrtxn", j, 3, resultSet.getString(11), UtilitiesPath.EXCEL_PATH);

						ladgerRows++;
					}
					
					if(ladgerRows>1) {
						excel.writeToExcel("utrtxn", j, 1, "CheckManually", UtilitiesPath.EXCEL_PATH);
					}
             
				} catch (Exception e) {
                     System.out.println(e.getMessage());
				} finally {
					resultSet.close();
					preparedStatement.close();
					connection.close();
				}
				System.out.println("Done: "+j);
			}
		} catch (Exception e) {
			 System.out.println(e.getMessage());
		} finally {
			
			excel.closeExcel();
		}
	}

}
