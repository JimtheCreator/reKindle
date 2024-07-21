package model;

public class SpareTime {

    String Hours, Level, Minutes, Name, Package, Pushid;

    public SpareTime() {
    }

    public SpareTime(String hours, String level, String minutes, String name, String aPackage, String pushid) {
        Hours = hours;
        Level = level;
        Minutes = minutes;
        Name = name;
        Package = aPackage;
        Pushid = pushid;
    }

    public String getHours() {
        return Hours;
    }

    public void setHours(String hours) {
        Hours = hours;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getMinutes() {
        return Minutes;
    }

    public void setMinutes(String minutes) {
        Minutes = minutes;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public String getPushid() {
        return Pushid;
    }

    public void setPushid(String pushid) {
        Pushid = pushid;
    }
}
