package serveur.sql;

import serveur.DataBase;
import serveur.Time;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * List of hours when the store is open
 */

public class TimeTable {

    private int storeId;

    private final List<TimeTableInterval> list;

    private TimeTable(ResultSet rs) throws Exception {
        list = new LinkedList<>();

        while(rs.next()){
            TimeTableInterval i = new TimeTableInterval(rs);
            list.add(i);
            storeId = i.getStore_id();
        }
    }

    /**
     * @param store the store id
     * @return  the timetable of this store
     */
    public static TimeTable getByStore(int store) throws Exception {
        try {
            ResultSet rs = DataBase.getInstance().getByCondition("TimeTable", "store_id", String.valueOf(store));
            return new TimeTable(rs);
        } catch (Exception e) {
            throw new Exception("Cannot get TimeTable of store " + store + "\n" + e.getMessage());
        }
    }

    /**
     * Add an openning on the database
     * @param open the opening
     * @param close the close
     * @param dayOfWeek the day of the week, 0-6
     */
    public void addInterval(Time open, Time close, int dayOfWeek) throws Exception {
        TimeTableInterval i = new TimeTableInterval(new TimeInterval(open, close), dayOfWeek, storeId);

        list.add(i);

        String sql = "INSERT INTO TimeTable (day, open, close, active, store_id) VALUES (?, ?, ?, ?, ?)";
        String[] tab = new String[]{
                String.valueOf(dayOfWeek),
                open.toString(),
                close.toString(),
                "1",
                String.valueOf(storeId)
        };
        try {
            DataBase.getInstance().query(sql, tab);
        } catch (Exception e) {
            throw new Exception("Cannot create Interval for TimeTable of " + storeId + "\n" + e.getMessage());
        }
    }

    /**
     * @param t a time
     * @param day   a day of week, 0-6
     * @return if the store is open at this date and time
     */
    public boolean isOpen(Time t, int day){
        for(TimeTableInterval i : list){
            if(i.getDayOfWeek() == day && i.isActive() && i.getTimeInterval().isIn(t))
                return true;
        }
        return false;
    }

    /**
     * @param day of week, 0-6
     * @return all the intervals of this day
     */
    public List<TimeTableInterval> getDay(int day){
        List<TimeTableInterval> l = new LinkedList<>();

        for(TimeTableInterval i : list){
            if(i.isActive() && i.getDayOfWeek() == day)
                l.add(i);
        }

        return l;
    }

    /**
     * @return all the intervals of the week
     */
    public List<TimeTableInterval> getAll(){
        return list;
    }

    /**
     * Remove all information about this store
     */
    public void clear() throws Exception {
        for(TimeTableInterval ti : list)
            ti.delete();
    }
}
