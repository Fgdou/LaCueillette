package server.sql;

import server.Common;
import server.DateTime;

public class Time extends DateTime {
    public Time(String str) throws Exception {
        super(str);
    }
    public Time(int hour, int min, int sec){
        super(0, 0, 0, hour, min, sec);
    }
    public Time(){
        super();
    }
    public String toString(){
        return Common.format(getHour(), 2) + ":" + Common.format(getMinute(), 2) + ":" + Common.format(getSecond(), 2);
    }
}