package FormatValidatorPrmg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayoutRefValidation {

	private static final String PAYOUTREF_REGEX = "^(?:(?:(?:19|20)\\d{2})(?:(?:0[13578]|1[02])(?:0[1-9]|[12]\\d|3[01])|(?:0[469]|11)(?:0[1-9]|[12]\\d|30)|02(?:0[1-9]|1\\d|2[0-8]))|(?:(?:19|20)(?:0[48]|[2468][048]|[13579][26])|2000)0229)(?:[01]\\d|2[0-3])(?:[0-5]\\d){2}[a-zA-Z0-9]{3,66}$";

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static void main(String[] args) {
		String payoutRef = "20240601130300abcd123";
		validatePayoutRefFormat(payoutRef);
	}

	public static void validatePayoutRefFormat(String input) {

		if (!matchesFormatWithInput(input)) {
			System.out.println("format not matching");
			return;
		}

		if (checkTimestampWithinFiveMinutes(input)) {
			System.out.println("Timestamp is valid and within 5 minutes of the current time.");
		} else {
			System.out.println("given timestamp should be less than 5mins");
		}
	}

	private static boolean matchesFormatWithInput(String input) {
		Pattern pattern = Pattern.compile(PAYOUTREF_REGEX);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	private static boolean checkTimestampWithinFiveMinutes(String input) {
		try {
			LocalDateTime givenTimeStamp = LocalDateTime.parse(input.substring(0, 14), FORMATTER);
			LocalDateTime currentTimeStamp = LocalDateTime.now();
			long minutesDifference = Math.abs(ChronoUnit.MINUTES.between(currentTimeStamp, givenTimeStamp));
			return minutesDifference < 5;
		} catch (Exception e) {
			System.out.println("Error parsing date: " + e.getMessage());
			return false;
		}
	}
}
