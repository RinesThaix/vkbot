package ru.ifmo.vkbot.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import ru.ifmo.vkbot.VkBot;

/**
 *
 * @author RinesThaix
 */
public class PostExecutor {

    @SuppressWarnings("deprecation")
    @Deprecated
    public static String executeGet(String urlGet) {
        try {
            URL url = new URL(urlGet);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                return reader.readLine();
            }
        } catch (Exception ex) {
            Logger.warn("Could not execute get-query!", ex);
            return null;
        }
    }

    public static InputStream execute(String urlGet) {
        try {
            String[] spl = urlGet.split("\\?");
            String urlClean = spl[0], params = spl[1];
            URL url = new URL(urlClean);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            byte[] out = params.getBytes("UTF-8");
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
            return http.getInputStream();
        } catch (Exception ex) {
            Logger.warn("Could not execute post-query!", ex);
            return null;
        }
    }

    public static String executeAndGet(String urlGet) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(execute(urlGet), "UTF-8"))) {
            String line = reader.readLine();
            if (line == null) {
                Logger.warn("Received unexpectable answer from " + urlGet);
            }
            return line;
        } catch (Exception ex) {
            Logger.warn("Could not execute and get post-query!", ex);
            return null;
        }
    }

    private final static String prefix = "https://api.vk.com/method/";
    private final static String access_token = "?access_token=";

    public static String buildAndGet(String method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(method).append(access_token).append(VkBot.getInstance().getAccessToken());
        for (int i = 0; i < params.length; i += 2) {
            sb.append("&").append(params[i]).append("=").append(params[i + 1]);
        }
        return executeAndGet(sb.toString());
    }

    public static String encode(String toEncode, Object... params) {
        return encode(String.format(toEncode, params));
    }

    public static String encode(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (Exception ex) {
            Logger.warn("Could not encode given text!");
            return toEncode;
        }
    }

}
