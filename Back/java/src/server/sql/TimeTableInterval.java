package server.sql;

import server.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeTableInterval {
    private TimeInterval inter;
    private int id, dayOfWeek, store_id;
    private boolean active;

    public TimeTableInterval(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        dayOfWeek = rs.getInt(2);
        inter = new TimeInterval(
                new Time(rs.getString(3)),
                new Time(rs.getString(4))
        );
        active = rs.getBoolean(5);
        store_id = rs.getInt(6);
    }
    public TimeTableInterval(TimeInterval interval, int dayOfWeek, int store){
        this.inter = interval;
        this.dayOfWeek = dayOfWeek;
        active = true;
        id = 0;
        store_id = store;
    }

    public TimeTableInterval getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("TimeTable", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Interval not found");
        return new TimeTableInterval(rs);
    }

    public TimeInterval getTimeInterval() {
        return inter;
    }

    public int getId() {
        return id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getStore_id() {
        return store_id;
    }

    public boolean isActive() {
        return active;
    }

    public void setInter(TimeInterval inter) throws Exception {
        this.inter = inter;
        DataBase.getInstance().changeValue("TimeTable", "open", inter.start.toString(), id);
        DataBase.getInstance().changeValue("TimeTable", "close", inter.stop.toString(), id);
    }

    public void setActive(boolean active) throws Exception {
        this.active = active;
        DataBase.getInstance().changeValue("TimeTable", "active", (active)?"1":"0", id);
    }
}