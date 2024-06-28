package LogChecker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import genricLibraries.ExcelUtility;

public class LogCheckingDemon {

	public static void main(String[] args) throws IOException {
		String filePath = "C:/Users/Guruprasad V/Desktop/LogCheck.txt";
		String excelPath = "C:/Users/Guruprasad V/Desktop/LogReport.xlsx";
		// JsonArray jsonArray = new JsonArray();
		Gson gson = new Gson();

		//ExcelUtility excel = new ExcelUtility();
		//excel.excelInit(excelPath);
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strTxnLog;
			// read log line by line using BufferedReader
			int i = 1;
			double totalTxn = 0;
			double processedTxn = 0;
			double failedTxn = 0;
			String firstTxnDate = "";
			String tempFirstTxnDate="";
			String lastTxnDate = "";
			int nullCount=0;
			
			while (true) {
				strTxnLog = br.readLine();
				try {
					//System.out.println(strTxnLog);
					// Parsing sting to json object
					
					int jsonStartIndex = strTxnLog.indexOf('{');
					nullCount=0;
					if (jsonStartIndex != -1) {
						totalTxn++;
						// Taking the timestamp
						if (totalTxn == 1) {
							firstTxnDate = strTxnLog.substring(0, 19);
						}
						lastTxnDate = strTxnLog.substring(0, 19);

						// System.out.println(timeStamp);

						String jsonPart = strTxnLog.substring(jsonStartIndex);
						JsonObject jsonObject = gson.fromJson(jsonPart, JsonObject.class);
						//System.out.println(String.valueOf(jsonObject)); 

						// Fetch the value using the keywords
						String event = jsonObject.get("event").getAsString();
						if (event.contains("Failed")) {
							failedTxn++;
						} else {
							processedTxn++;
						}
					}
				} catch (Exception e) {
					nullCount++;
					if (nullCount == 1) {
						System.out.println(firstTxnDate + " " + lastTxnDate);
//						System.out.println(timeDifferenceChecker(firstTxnDate, lastTxnDate));
						DecimalFormat decimalFormat = new DecimalFormat("#.####");
						double tps = totalTxn / timeDifferenceChecker(firstTxnDate, lastTxnDate);
						System.out.println("Total txns: " + totalTxn);
						System.out.println("Processed txns: " + processedTxn);
						System.out.println("Failed txns: " + failedTxn);
						System.out.println("Tps: " + decimalFormat.format(tps));
					    System.out.println("==============================================");
						// double gcd=gcd(processedTxn, failedTxn);
						// System.out.println("Pass:Failure ratio:
						// "+processedTxn/gcd+":"+failedTxn/gcd);
						totalTxn=0;
						failedTxn=0;
						processedTxn=0;
					}
					TimeUnit.SECONDS.sleep(1);
				}

			}

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			fstream.close();
			//excel.closeExcel();
		}
	}

	public static long timeDifferenceChecker(String start, String end) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime startTime = LocalDateTime.parse(start, formatter);
		LocalDateTime endTime = LocalDateTime.parse(end, formatter);

		Duration duration = Duration.between(startTime, endTime);

		return duration.getSeconds();

	}

	public static double gcd(double a, double b) {
		while (b != 0) {
			double temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

}
