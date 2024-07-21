package model;

import java.util.Comparator;

public class Journal {
    String Date, Habitname, Journalid, Userid, Writtentxt, Month, Day;
    long Timeinmillis;


    public Journal() {
    }


    public Journal(String date, String habitname, String journalid, String userid, String writtentxt, String month, String day, long timeinmillis) {
        Date = date;
        Habitname = habitname;
        Journalid = journalid;
        Userid = userid;
        Writtentxt = writtentxt;
        Month = month;
        Day = day;
        Timeinmillis = timeinmillis;
    }

    public static Comparator<Journal> datesorting = new Comparator<Journal>() {
        @Override
        public int compare(Journal journal, Journal t1) {
            return (int) (journal.getTimeinmillis() - t1.getTimeinmillis());
        }
    };

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getHabitname() {
        return Habitname;
    }

    public void setHabitname(String habitname) {
        Habitname = habitname;
    }

    public String getJournalid() {
        return Journalid;
    }

    public void setJournalid(String journalid) {
        Journalid = journalid;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getWrittentxt() {
        return Writtentxt;
    }

    public void setWrittentxt(String writtentxt) {
        Writtentxt = writtentxt;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public long getTimeinmillis() {
        return Timeinmillis;
    }

    public void setTimeinmillis(long timeinmillis) {
        Timeinmillis = timeinmillis;
    }

    public static Comparator<Journal> getDatesorting() {
        return datesorting;
    }

    public static void setDatesorting(Comparator<Journal> datesorting) {
        Journal.datesorting = datesorting;
    }
}
