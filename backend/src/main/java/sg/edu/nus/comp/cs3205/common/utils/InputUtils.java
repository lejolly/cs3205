package sg.edu.nus.comp.cs3205.common.utils;

public class InputUtils {

    private static final String noWhitespace = "^[a-zA-Z0-9_.-]+$";
    private static final String hasWhitespace = "^[a-zA-Z0-9_. -]+$";
    private static final String numbersOnly = "^[0-9]+$";
    private static final String forHashSaltOtpSeedResponse = "^[a-zA-Z0-9$=/+]+$";

    public static boolean noWhitespace(String s) {
        return s.matches(noWhitespace);
    }

    public static boolean withWhitespace(String s) {
        return s.matches(hasWhitespace);
    }

    public static boolean numbersOnly(String s) {
        return s.matches(numbersOnly);
    }

    public static boolean forHashSaltOtpSeedResponse(String s) {
        return s.matches(forHashSaltOtpSeedResponse);
    }

}
