import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Convert {

    // Checks if its double with error catching
    public static boolean isNumeric(String x) {
        if (x == null) {
            return false;
        }
        try {
            Double.parseDouble(x);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Checks if it is double with error checking and also returns the double
    public static double getIfNumeric(String x) {
        if (isNumeric(x)) {
            return Double.parseDouble(x);
        } else {
            return Integer.MIN_VALUE;
        }
    }

    // Converts 1 to True else to False
    public static boolean getBoolean(String x) {
        if(isNumeric(x)){
            return getIfNumeric(x) == 1;
        }else{
            return x.equalsIgnoreCase("true");
        }
    }

    // Converts data to a date if it is a valid date, contains error checking
    public static LocalDate getIfDate(String x) {
        if (x == null) return LocalDate.of(2000, 1, 1);
        try {
            return LocalDate.parse(x.substring(0, 10));
        } catch (Exception e) {
            return LocalDate.of(2000, 1, 1);
        }
    }

}
