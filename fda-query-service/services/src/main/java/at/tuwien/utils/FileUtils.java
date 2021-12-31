package at.tuwien.utils;

public class FileUtils {

    public static boolean isUrl(String data) {
        return data.startsWith("http") || data.startsWith("https");
    }

    public static boolean isTestFile(String data) {
        return data.startsWith("test:");
    }
}
