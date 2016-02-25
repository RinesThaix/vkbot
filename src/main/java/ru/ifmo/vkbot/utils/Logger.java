package ru.ifmo.vkbot.utils;

import java.util.Date;

/**
 *
 * @author RinesThaix
 */
@SuppressWarnings("deprecation")
public class Logger {

    private static void write(String s) {
        System.out.println(s);
    }

    private static String getTime() {
        Date t = new Date();
        t.setTime(System.currentTimeMillis());
        return t.toLocaleString();
    }
    
    public static void log(String s, Object... args) {
        log(String.format(s, args));
    }

    public static void log(String s) {
        StringBuilder sb = new StringBuilder();
        write(sb
                .append("[")
                .append(Thread.currentThread().getName())
                .append("/INFO] ")
                .append(getTime())
                .append(": ")
                .append(s).toString());
    }
    
    public static void debug(String s) {
        log("[Debug] " + s);
    }

    public static void warn(String s) {
        warn(s, null);
    }

    public static void warn(String s, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        write(sb
                .append("[")
                .append(Thread.currentThread().getName())
                .append("/WARNING] ")
                .append(getTime())
                .append(": ")
                .append(s).toString());
        sb = new StringBuilder();
        sb.append("[")
                .append(Thread.currentThread().getName())
                .append("/EXCEPTION] ");
        String prefix = sb.toString();
        if (ex != null) {
            write(prefix + "Cause: " + ex.getMessage());
//            for (StackTraceElement element : ex.getStackTrace()) {
//                write(prefix + element.toString());
//            }
            ex.printStackTrace();
        }
    }

}
