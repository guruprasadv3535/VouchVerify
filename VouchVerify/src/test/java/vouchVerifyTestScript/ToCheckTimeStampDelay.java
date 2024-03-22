package vouchVerifyTestScript;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.testng.annotations.Test;

import genricLibraries.ExcelUtility;
import genricLibraries.PropertiesUtility;
import genricLibraries.UtilitiesPath;

public class ToCheckTimeStampDelay {

	// @Test
	public String toConvertUSTtoIST(String ustDate) {
		// Define the date time format for input and output
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yy h:mm:ss.SSSSSSSSS a");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, h:mm:ss a");

		// Parse the UTC timestamp
		LocalDateTime utcTimestamp = LocalDateTime.parse(ustDate, inputFormatter);

		// Convert to IST
		ZonedDateTime utcDateTime = ZonedDateTime.of(utcTimestamp, ZoneId.of("UTC"));
		ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

		// Print the result
		System.out.println("UTC Timestamp: " + utcTimestamp.format(inputFormatter));
		System.out.println("IST Timestamp: " + istDateTime.format(outputFormatter));
		return istDateTime.format(outputFormatter);
	}

//	@Test
	public void toCheckTheBulkDelay() {

		// creating the instance to property and excel classes and initializing it
		PropertiesUtility property = new PropertiesUtility();
		ExcelUtility excel = new ExcelUtility();
		property.propertiesInit(UtilitiesPath.PROPERTIES_PATH);
		excel.excelInit(UtilitiesPath.EXCEL_PATH);
        System.out.println(excel.getLastRowNum("delay"));
		// Define the date time format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, h:mm:ss a");

		// Parse the timestamps
		for (int i = 0; i <= excel.getLastRowNum("delay"); i++) {
			
			if(!(excel.readDataFromExcel("delay",i,2).equals("null"))) {
				
				String apiDate=excel.readDataFromExcel("delay", i, 1);
				String webhookDate=excel.readDataFromExcel("delay", i, 2).replace("PM", "pm");
				LocalDateTime timestamp1 = LocalDateTime.parse(apiDate, formatter);
				LocalDateTime timestamp2 = LocalDateTime.parse(toConvertUSTtoIST(webhookDate), formatter);
				
				// Calculate the time delay
				Duration duration = Duration.between(timestamp1, timestamp2);
				
				//delay in seconds
				String delay=formatDuration(duration);
				excel.writeToExcel("delay", i, 4, delay, UtilitiesPath.EXCEL_PATH);
			}else {
				excel.writeToExcel("delay", i, 4, "null", UtilitiesPath.EXCEL_PATH);
			}
			System.out.println("done: "+i);
		}
		
		excel.closeExcel();
	}
	
	@Test
	public void toCheckSingleDelay() {
		
		// Define the date time format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy h:mm:ss.SSSSSSSSS a");
				
		String apiDate="20-12-23 2:30:21.999730000 PM";
		String webhookDate="20-12-23 2:30:39.058359000 PM";
		LocalDateTime timestamp1 = LocalDateTime.parse(apiDate.toLowerCase(), formatter);
		LocalDateTime timestamp2 = LocalDateTime.parse(webhookDate.toLowerCase(), formatter);
		
		// Calculate the time delay
		Duration duration = Duration.between(timestamp1, timestamp2);
		
		//delay in seconds
		String delay=formatDuration(duration);
		System.out.println(delay);
	}

	public String formatDuration(Duration duration) {

		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.getSeconds();
		long millis = duration.toMillisPart();

		// return String.valueOf(seconds);
		return String.format("%d seconds", seconds);
	}

}
