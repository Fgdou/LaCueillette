package serveur.sql;

import serveur.DateTime;

public class TimeInterval {
    public final DateTime start;
    public final DateTime stop;

    public TimeInterval(DateTime start, DateTime stop){
        this.start = start;
        this.stop = stop;
    }

    public boolean isIn(DateTime time){
        return start.compareTo(time) <= 0 && stop.compareTo(time) >= 0;
    }

    public boolean equals(Object o){
        return (o instanceof TimeInterval &&
                ((TimeInterval) o).start.equals(start) && ((TimeInterval) o).stop.equals(stop));
    }
}
