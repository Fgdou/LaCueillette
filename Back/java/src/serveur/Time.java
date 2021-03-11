package serveur;

import serveur.Common;
import serveur.DateTime;

/**
 * Store the time in the day
 */

public class Time extends DateTime {
    /**
     * @param str on format "hh:mm:ss"
     */
    public Time(String str) throws Exception {
        super("0000-00-00 " + str);
    }

    /**
     * @param hour 0 to 23
     * @param min  0 to 59
     * @param sec  0 to 59
     * @note if the number exceed, the time will be added
     */
    public Time(int hour, int min, int sec){
        super(0, 0, 0, hour, min, sec);
    }
    public String toString(){
        return Common.format(getHour(), 2) + ":" + Common.format(getMinute(), 2) + ":" + Common.format(getSecond(), 2);
    }
}