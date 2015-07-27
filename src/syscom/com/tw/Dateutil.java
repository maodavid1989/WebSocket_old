package syscom.com.tw;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String getYYYY_MM_DD(){
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		return sdfDate.format(now);
	}
}
