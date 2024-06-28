package LogChecker;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Simulator {

	public static void main(String[] args) throws InterruptedException {
		String filePath = "C:/Users/Guruprasad V/Desktop/LogCheck.txt";

		for(int i=1;;i++) {
			Map<String, Object> responseBody = new LinkedHashMap<>();
			responseBody.put("env", "sandbox");
			responseBody.put("event", "");
			Random random = new Random();
			responseBody.put("transaction_id", "fund" + (random.nextInt(99999999 - 10000000 + 1) + 10000000));
			responseBody.put("corpCode", "PHEDORA");
			responseBody.put("statusDescription", "Invalid Currency");
			responseBody.put("batchNo", "fund" + (random.nextInt(9999 - 1000 + 1) + 1000));
			responseBody.put("utrNo", random.nextLong(824914708000L, 824914709999L + 1));
			responseBody.put("processingDate", universalTimeStamp());
			responseBody.put("crn", "Bloomcart020240610101154Imps017");
			responseBody.put("transactionStatus", "");
			responseBody.put("responseCode", "0");
			responseBody.put("paymentMode", "A");
			responseBody.put("vendorCode", "Term Inc.");
			responseBody.put("amount", "100.00");
			responseBody.put("corporateAccountNumber", "924020027157005");
			responseBody.put("debitCreditIndicator", "D");
			responseBody.put("beneficiaryAccountNumber", "05201010032452245");
			
			if(random.nextInt(0,10)<=5) {
				responseBody.put("event", "Failed Payout");
				responseBody.put("transactionStatus", "REJECTED");
			}else {
				responseBody.put("event", "Processed Payout");
				responseBody.put("transactionStatus", "PROCESSED");
			}
			
			String response=timeStampGenrator()+":[info]:"+ convertToJson(responseBody);
			saveResponseToFile(response, filePath);
			  System.out.println("file updated "+i);
			  Thread.sleep(100);
		}
		
		
	}
	
	// To save the response into text file
	public static void saveResponseToFile(String response, String filePath) {
		try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.write(response);
            fileWriter.write(System.lineSeparator()); // Add a new line after the data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	// To generate IST current timestamp
	public static String timeStampGenrator() {

		LocalDateTime currentDateTime = LocalDateTime.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return currentDateTime.format(formatter);
	}

	// To generate UTC timestamp
	public static String universalTimeStamp() {

		LocalDateTime utcDateTime = LocalDateTime.now(ZoneOffset.UTC);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return utcDateTime.format(formatter);
	}

	// To convert UTC to IST
	public static String utcToISTtimestampConvertor(String dateTimeStr) {

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime utcDateTime = LocalDateTime.parse(dateTimeStr, inputFormatter);

		ZoneId istZone = ZoneId.of("Asia/Kolkata");
		LocalDateTime istDateTime = utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(istZone).toLocalDateTime();

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		return istDateTime.format(outputFormatter);
	}

	// To convert the string to json object
	private static String convertToJson(Map<String, Object> payload) {
		// For simplicity, converting to a string manually
		StringBuilder jsonBuilder = new StringBuilder("{");

		for (Map.Entry<String, Object> entry : payload.entrySet()) {
			jsonBuilder.append("\"").append(entry.getKey()).append("\":");
			if (entry.getValue() instanceof String) {
				jsonBuilder.append("\"").append(entry.getValue()).append("\",");
			} else {
				jsonBuilder.append(entry.getValue()).append(",");
			}
		}
		// Remove the trailing comma if there are entries
		if (payload.size() > 0) {
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
		}

		jsonBuilder.append("}");

		return jsonBuilder.toString();
	}
}
