package model;

public class Apps {
    String Packagename, Appid;

    public Apps() {

    }

    public Apps(String packagename, String appid) {
        Packagename = packagename;
        Appid = appid;
    }

    public String getPackagename() {
        return Packagename;
    }

    public void setPackagename(String packagename) {
        Packagename = packagename;
    }

    public String getAppid() {
        return Appid;
    }

    public void setAppid(String appid) {
        Appid = appid;
    }
}
