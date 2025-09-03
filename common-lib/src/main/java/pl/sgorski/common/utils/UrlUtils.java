package pl.sgorski.common.utils;

public class UrlUtils {

    public static String buildUrl(String url, String port) {
        return port != null ? url + ":" + port : url;
    }
}
