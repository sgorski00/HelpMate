package pl.sgorski.common.utils;

import org.springframework.util.StringUtils;

public class UrlUtils {

    public static String buildUrl(String url, String port) {
        return StringUtils.hasText(port) ? url + ":" + port : url;
    }
}
