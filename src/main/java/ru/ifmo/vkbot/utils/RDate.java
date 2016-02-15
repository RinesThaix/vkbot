package ru.ifmo.vkbot.utils;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author Константин
 */
@SuppressWarnings("deprecation")
public class RDate extends Date {
    
    public RDate() {
        this(System.currentTimeMillis());
    }
    
    public RDate(long time) {
        super(time);
    }
    
    public String toGMT() {
        return toGMTString();
    }
    
    public String toLocale() {
        return toLocaleString();
    }
    
    public String getMonthString() {
        return getMonthString(getMonth());
    }
    
    private String getMonthString(int month) {
        switch(month) {
            case 1:
                return "Января";
            case 2:
                return "Февраля";
            case 3:
                return "Марта";
            case 4:
                return "Апреля";
            case 5:
                return "Мая";
            case 6:
                return "Июня";
            case 7:
                return "Июля";
            case 8:
                return "Августа";
            case 9:
                return "Сентября";
            case 10:
                return "Октября";
            case 11:
                return "Ноября";
            case 12:
                return "Декабря";
            default:
                return getMonth() + "-го месяца";
        }
    }
    
    @Override
    public int getHours() {
        for(int i = 0; i < 3; ++i)
            addHour();
        String gmt = toGMT();
        String[] spl = gmt.split(" ");
        int hours = Integer.valueOf(spl[spl.length - 2].split(":")[0]);
        for(int i = 0; i < 3; ++i)
            takeHour();
        return hours;
    }
    
    @Override
    public int getDay() {
        for(int i = 0; i < 3; ++i)
            addHour();
        int day = Integer.valueOf(toGMT().split(" ")[0]);
        for(int i = 0; i < 3; ++i)
            takeHour();
        return day;
    }
    
    @Override
    public int getMonth() {
        for(int i = 0; i < 3; ++i)
            addHour();
        int month = super.getMonth();
        for(int i = 0; i < 3; ++i)
            takeHour();
        return month + 1;
    }
    
    @Override
    public int getYear() {
        for(int i = 0; i < 3; ++i)
            addHour();
        int year = super.getYear();
        for(int i = 0; i < 3; ++i)
            takeHour();
        return year + 1900;
    }
    
    @Override
    public String toString() {
        int day = getDay(), month = getMonth(), year = getYear(), hh = getHours(), mm = getMinutes(), ss = getSeconds();
        String hours = hh < 10 ? "0" + hh : hh + "",
                minutes = mm < 10 ? "0" + mm : mm + "",
                seconds = ss < 10 ? "0" + ss : ss + "";
        return String.format("%d %s %d года, %s:%s:%s", day, getMonthString(month), year, hours, minutes, seconds);
    }
    
    public String toString(boolean with_year, boolean with_hours) {
        if(with_year && with_hours)
            return toString();
        int day = getDay(), month = getMonth(), year = getYear(), hh = getHours(), mm = getMinutes(), ss = getSeconds();
        String hours = hh < 10 ? "0" + hh : hh + "",
                minutes = mm < 10 ? "0" + mm : mm + "",
                seconds = ss < 10 ? "0" + ss : ss + "";
        if(!with_year && !with_hours)
            return String.format("%d %s", day, getMonthString(month));
        if(!with_year)
            return String.format("%d %s, %s:%s:%s", day, getMonthString(month), hours, minutes, seconds);
        return String.format("%d %s %d года", day, getMonthString(month), year);
    }
    
    public void addTick() {
        setTime(getTime() + 50);
    }
    
    public void takeTick() {
        setTime(getTime() - 50);
    }
    
    public void addSecond() {
        setTime(getTime() + 1000);
    }
    
    public void takeSecond() {
        setTime(getTime() - 1000);
    }
    
    public void addMinute() {
        setTime(getTime() + 60000);
    }
    
    public void takeMinute() {
        setTime(getTime() - 60000);
    }
    
    public void addHour() {
        setTime(getTime() + 3600000);
    }
    
    public void takeHour() {
        setTime(getTime() - 3600000);
    }
    
    public void addDays(int days) {
        setTime(getTime() + 86400000l * days);
    }
    
    public void addDay() {
        setTime(getTime() + 86400000);
    }
    
    public void takeDay() {
        setTime(getTime() - 86400000);
    }
    
    public void addWeek() {
        setTime(getTime() + 604800000);
    }
    
    public void takeWeek() {
        setTime(getTime() - 604800000);
    }
    
    public void addMonth() {
        setTime(getTime() + 2592000000l);
    }
    
    public void takeMonth() {
        setTime(getTime() - 2592000000l);
    }
    
    public void addYear() {
        setTime(getTime() + 31536000000l);
    }
    
    public void takeYear() {
        setTime(getTime() - 31536000000l);
    }
    
    public void addCentury() {
        setTime(getTime() + 3153600000000l);
    }
    
    public void takeCentury() {
        setTime(getTime() - 3153600000000l);
    }
    
    public void addMillenium() {
        setTime(getTime() + 31536000000000l);
    }
    
    public void takeMillenium() {
        setTime(getTime() - 31536000000000l);
    }
    
    public String getDayOfTheWeek() {
        int day = super.getDay();
        switch(day) {
            case 0: return "воскресенье";
            case 1: return "понедельник";
            case 2: return "вторник";
            case 3: return "среда";
            case 4: return "четверг";
            case 5: return "пятница";
            case 6: return "суббота";
            default: return null;
        }
    }
    
}
