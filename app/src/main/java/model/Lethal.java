package model;

public class Lethal {

    boolean Islimited;
    String Package, Hours, Minutes;
    long Lastchecked;

    public Lethal() {

    }

    public Lethal(boolean islimited, String aPackage, String hours, String minutes, long lastchecked) {
        Islimited = islimited;
        Package = aPackage;
        Hours = hours;
        Minutes = minutes;
        Lastchecked = lastchecked;
    }

    public boolean isIslimited() {
        return Islimited;
    }

    public void setIslimited(boolean islimited) {
        Islimited = islimited;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public String getHours() {
        return Hours;
    }

    public void setHours(String hours) {
        Hours = hours;
    }

    public String getMinutes() {
        return Minutes;
    }

    public void setMinutes(String minutes) {
        Minutes = minutes;
    }

    public long getLastchecked() {
        return Lastchecked;
    }

    public void setLastchecked(long lastchecked) {
        Lastchecked = lastchecked;
    }
}
