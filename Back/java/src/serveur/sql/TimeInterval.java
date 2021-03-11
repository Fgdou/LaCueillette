package serveur.sql;

import serveur.DateTime;

/**
 * Store a start and end during the day
 */

public class TimeInterval {
    public final DateTime start;
    public final DateTime stop;

    /**
     * @param start the beginning
     * @param stop  the end
     */
    public TimeInterval(DateTime start, DateTime stop){
        this.start = start;
        this.stop = stop;
    }

    /**
     * @param time a time
     * @return  if the time is in the interval
     */
    public boolean isIn(DateTime time){
        return start.compareTo(time) <= 0 && stop.compareTo(time) >= 0;
    }

    public boolean equals(Object o){
        return (o instanceof TimeInterval &&
                ((TimeInterval) o).start.equals(start) && ((TimeInterval) o).stop.equals(stop));
    }
}
