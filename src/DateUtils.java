import java.text.SimpleDateFormat;
import java.util.Calendar;

class DateUtils {
    public static final String DATE_FORMAT_NOW = "HH:mm:ss 'on' dd MMM, yyyy";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
}