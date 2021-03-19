package serveur;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Store the date and time on format 'yyyy-MM-dd hh:mm:ss'
 */

public class DateTime {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    /**
     * @param time      Timestamp
     */
    public DateTime(long time) throws Exception {
        Date d = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        parse(sd.format(d));
        checkValidity();
    }
    /**
     * Create a date now
     */
    public DateTime(){
        Date d = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            parse(sd.format(d));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        checkValidity();
    }
    /**
     * Create a copy of the date
     * @param dt    The date
     */
    public DateTime(DateTime dt){
        year = dt.year;
        month = dt.month;
        day = dt.day;
        hour = dt.hour;
        minute = dt.minute;
        second = dt.second;
        checkValidity();
    }
    /**
     * Create a date with a String
     * @param str   Must be 'yyyy-MM-dd hh:mm:ss'
     */
    public DateTime(String str) throws Exception {
        parse(str);
        checkValidity();
    }
    /**
     * Create a date
     */
    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month-1;
        this.day = day-1;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        checkValidity();
    }

    /**
     * Apply the string to the date
     * @param str   Must be 'yyyy-MM-dd hh:mm:ss'
     */
    private void parse(String str) throws Exception {
        //yyyy-MM-dd hh:mm:ss
        if(str == null)
            return;

        if(!str.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"))
            throw new Exception("The date format is not 'yyyy-MM-dd hh:mm:ss'");

        year = Integer.parseInt(str.substring(0, 4));
        month = Integer.parseInt(str.substring(5, 7))-1;
        day = Integer.parseInt(str.substring(8, 10))-1;
        hour = Integer.parseInt(str.substring(11, 13));
        minute = Integer.parseInt(str.substring(14, 16));
        second = Integer.parseInt(str.substring(17, 19));
    }

    /**
     * @return      The 2 dates added
     */
    public DateTime add(int year, int month, int day, int hour, int minute, int second){
        return new DateTime(year+this.year, month +this.month+1, day+this.day+1, hour+this.hour, minute+this.minute, second+this.second);
    }
    /**
     * If the date is not valid (second >= 60), then increment the date
     */
    private void checkValidity(){
        while (second >= 60){
            second -= 60;
            minute++;
        }
        while(minute >= 60){
            minute -= 60;
            hour++;
        }
        while(hour >= 24){
            hour -= 24;
            day++;
        }
        while(day >= getLastDayOfMonth()){
            day -= getLastDayOfMonth();
            month++;

            while(month >= 12){
                month -= 12;
                year++;
            }
        }
        while(month >= 12){
            month -= 12;
            year++;
        }
    }
    /**
     * @param other     The date to compare
     * @return  1 if this >= other | -1 if this < other | 0 if this == other
     */
    public int compareTo(DateTime other){
        // this >= other ---> this - other >= 0
        if(toLong() - other.toLong() > 0)
            return 1;
        if(toLong() - other.toLong() < 0)
            return -1;
        return 0;
    }

    /**
     * @param other     The date to compare
     * @return          If the date are equals
     */
    public boolean equals(DateTime other){
        return compareTo(other) == 0;
    }
    /**
     * French : bissextile
     * @return  If the year is leap
     */
    public boolean leap(){
        return (year%4 == 0 && year%100 != 0 || year%400 == 0);
    }

    /**
     * @return  A non standard timestamp long which represent the date
     */
    private long toLong(){
        return second + minute* 60L + hour*3600L + day*24*3600L + month*31*24*3600L + year * 12*31*24*3600L;
    }
    public String toString(){
        return Common.format(year, 4)+"-"+Common.format(month+1, 2)+"-"+Common.format(day+1, 2)+" "+Common.format(hour, 2)+":"+Common.format(minute, 2)+":"+Common.format(second, 2);
    }

    /**
     * @return the last day of the actual month
     */
    public int getLastDayOfMonth(){
        if(month == 1)
            return leap() ? 29 : 28;
        if(month < 7 && month%2 == 0)
            return 31;
        if(month >= 7 && month%2 == 1)
            return 31;
        return 30;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
