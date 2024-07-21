package model;


public class AppDetails {

    private String Applogo;
    private String Appname;
    private String Endtime;
    private String Starttime;
    private String Packagename;
    private String Taskid;
    private boolean Isclicked;
    private String Appid;


    public AppDetails() {

    }


    public AppDetails(String applogo, String appname, String endtime, String starttime, String packagename, String taskid, boolean isclicked, String appid) {
        Applogo = applogo;
        Appname = appname;
        Endtime = endtime;
        Starttime = starttime;
        Packagename = packagename;
        Taskid = taskid;
        Isclicked = isclicked;
        Appid = appid;
    }


    public String getApplogo() {
        return Applogo;
    }

    public void setApplogo(String applogo) {
        Applogo = applogo;
    }

    public String getAppname() {
        return Appname;
    }

    public void setAppname(String appname) {
        Appname = appname;
    }

    public String getEndtime() {
        return Endtime;
    }

    public void setEndtime(String endtime) {
        Endtime = endtime;
    }

    public String getStarttime() {
        return Starttime;
    }

    public void setStarttime(String starttime) {
        Starttime = starttime;
    }

    public String getPackagename() {
        return Packagename;
    }

    public void setPackagename(String packagename) {
        Packagename = packagename;
    }

    public String getTaskid() {
        return Taskid;
    }

    public void setTaskid(String taskid) {
        Taskid = taskid;
    }

    public boolean isIsclicked() {
        return Isclicked;
    }

    public void setIsclicked(boolean isclicked) {
        Isclicked = isclicked;
    }

    public String getAppid() {
        return Appid;
    }

    public void setAppid(String appid) {
        Appid = appid;
    }
}
