package pl.sgorski.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlUtilsTests {

    @Test
    void shouldBuildUrlWithPort() {
        String url = "http://localhost";
        String port = "8080";

        String result = UrlUtils.buildUrl(url, port);

        assertEquals("http://localhost:8080", result);
    }

    @Test
    void shouldBuildUrlWithoutPort_Null() {
        String url = "http://localhost";

        String result = UrlUtils.buildUrl(url, null);

        assertEquals("http://localhost", result);
    }

    @Test
    void shouldBuildUrlWithoutPort_Blank() {
        String url = "http://localhost";
        String port = "   ";

        String result = UrlUtils.buildUrl(url, port);

        assertEquals("http://localhost", result);
    }
}
