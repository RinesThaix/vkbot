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
    
    private static void write(String s, Object... args) {
        write(String.format(s, args));
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
        write("[%s/INFO] %s: %s", Thread.currentThread().getName(), getTime(), s);
    }
    
    public static void debug(String s) {
        log("[Debug] " + s);
    }
    
    public static void debug(String s, Object... args) {
        debug(String.format(s, args));
    }

    public static void warn(String s) {
        warn(s, null); 
    }

    public static void warn(String s, Throwable ex) {
        write("[%s/WARNING] %s: %s", Thread.currentThread().getName(), getTime(), s);
        if(ex != null) {
            write("[%s/EXCEPTION] ", Thread.currentThread().getName());
            ex.printStackTrace();
        }
    }

}
