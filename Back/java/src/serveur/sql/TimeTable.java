package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

//TODO tests
public class TimeTable {

    private int storeId;

    private final List<TimeTableInterval> list;

    public TimeTable(int store){
        list = new LinkedList<>();
        storeId = store;
    }
    private TimeTable(ResultSet rs) throws Exception {
        list = new LinkedList<>();

        while(rs.next()){
            TimeTableInterval i = new TimeTableInterval(rs);
            list.add(i);
            storeId = i.getStore_id();
        }
    }

    public static TimeTable getByStore(int store) throws Exception {
        try {
            ResultSet rs = DataBase.getInstance().getByCondition("TimeTable", "store_id", String.valueOf(store));
            return new TimeTable(rs);
        } catch (Exception e) {
            throw new Exception("Cannot get TimeTable of store " + store + "\n" + e.getMessage());
        }
    }

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
    public boolean isOpen(Time t){
        for(TimeTableInterval i : list){
            if(i.isActive() && i.getTimeInterval().isIn(t))
                return true;
        }
        return false;
    }
    public List<TimeInterval> getDay(int day){
        List<TimeInterval> l = new LinkedList<>();

        for(TimeTableInterval i : list){
            if(i.isActive() && i.getDayOfWeek() == day)
                l.add(i.getTimeInterval());
        }

        return l;
    }
}
