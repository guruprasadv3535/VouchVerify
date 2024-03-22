package genricLibraries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.testng.annotations.Test;

/*
 * This class contains reusable methods to perform java related actions
 * @author Guruprasad
 */

public class JavaUtilitiy {

	/*
	 * This method generates random number within the limit specified
	 * 
	 * @param limit
	 * 
	 * @return
	 */
	public int generateRandomNum(int limit) {

		Random random = new Random();
		return random.nextInt(limit);
	}

	/*
	 * This method generates currents time
	 * 
	 * @return
	 */
	public String getCurrentTime() {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
		return sdf.format(date);
	}
		
}
