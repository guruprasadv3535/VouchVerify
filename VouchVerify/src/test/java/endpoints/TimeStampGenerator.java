package endpoints;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeStampGenerator {

//	This method is used to generate the current time and date in given format

	public static String generateTimestamp() {
		// Set the desired time zone (Asia/Kolkata)
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Kolkata");

		// Create a SimpleDateFormat instance with the desired format
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy, h:mm:ss a");
		sdf.setTimeZone(timeZone);

		// Get the current date and time
		Date currentDate = new Date();

		// Format the date and time as per the SimpleDateFormat
		return sdf.format(currentDate);
	}

}
