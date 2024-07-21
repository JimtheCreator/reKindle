package model;

public class Subscription {

    String Package, Timepaid, Userid;

    public Subscription(String aPackage, String timepaid, String userid) {
        Package = aPackage;
        Timepaid = timepaid;
        Userid = userid;
    }

    public Subscription() {
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public String getTimepaid() {
        return Timepaid;
    }

    public void setTimepaid(String timepaid) {
        Timepaid = timepaid;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }
}
