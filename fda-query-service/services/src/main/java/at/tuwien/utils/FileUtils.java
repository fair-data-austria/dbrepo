package at.tuwien.utils;

/**
 * This class is used to determine if the csv source is either an URI, test file or within the classpath (by contradicting the other two)
 */
public class FileUtils {

    public static boolean isUrl(String data) {
        return data.startsWith("http") || data.startsWith("https");
    }

    public static boolean isTestFile(String data) {
        return data.startsWith("test:");
    }
}
