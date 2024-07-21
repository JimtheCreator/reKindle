package model;

import java.util.Comparator;

public class Event {

    String Date, Event, Eventid, Partnerid, Userid;
    long Timeinmillis;

    public Event() {

    }


    public Event(String date, String event, String eventid, String partnerid, String userid, long timeinmillis) {
        Date = date;
        Event = event;
        Eventid = eventid;
        Partnerid = partnerid;
        Userid = userid;
        Timeinmillis = timeinmillis;
    }


    public static Comparator<Event> datesorting = new Comparator<model.Event>() {
        @Override
        public int compare(model.Event event, model.Event t1) {
//            try {
//                return (int) (event.getTimeinmillis() - t1.getTimeinmillis());
//            } catch (Exception e) {
//                e.getMessage();
//            }

            return (int) (event.getTimeinmillis() - t1.getTimeinmillis());
        }
    };



    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getEventid() {
        return Eventid;
    }

    public void setEventid(String eventid) {
        Eventid = eventid;
    }

    public String getPartnerid() {
        return Partnerid;
    }

    public void setPartnerid(String partnerid) {
        Partnerid = partnerid;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public long getTimeinmillis() {
        return Timeinmillis;
    }

    public void setTimeinmillis(long timeinmillis) {
        Timeinmillis = timeinmillis;
    }

    public static Comparator<model.Event> getDatesorting() {
        return datesorting;
    }

}
