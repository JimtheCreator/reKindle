package model;

public class Users {

    String Email, Name, Profilepic, Time, Tostart, Userid, Timemillis, Package, Timepaid, Expiring, Token;
    boolean Isgoogle, Ishabit, Ispaid, Iskillerswitch, Iscollection, Issigned;


    public Users() {
    }


    public Users(String email, String name, String profilepic, String time, String tostart, String userid, String timemillis, String aPackage, String timepaid, String expiring, String token, boolean isgoogle, boolean ishabit, boolean ispaid, boolean iskillerswitch, boolean iscollection, boolean issigned) {
        Email = email;
        Name = name;
        Profilepic = profilepic;
        Time = time;
        Tostart = tostart;
        Userid = userid;
        Timemillis = timemillis;
        Package = aPackage;
        Timepaid = timepaid;
        Expiring = expiring;
        Token = token;
        Isgoogle = isgoogle;
        Ishabit = ishabit;
        Ispaid = ispaid;
        Iskillerswitch = iskillerswitch;
        Iscollection = iscollection;
        Issigned = issigned;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfilepic() {
        return Profilepic;
    }

    public void setProfilepic(String profilepic) {
        Profilepic = profilepic;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTostart() {
        return Tostart;
    }

    public void setTostart(String tostart) {
        Tostart = tostart;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getTimemillis() {
        return Timemillis;
    }

    public void setTimemillis(String timemillis) {
        Timemillis = timemillis;
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

    public String getExpiring() {
        return Expiring;
    }

    public void setExpiring(String expiring) {
        Expiring = expiring;
    }

    public boolean isIsgoogle() {
        return Isgoogle;
    }

    public void setIsgoogle(boolean isgoogle) {
        Isgoogle = isgoogle;
    }

    public boolean isIshabit() {
        return Ishabit;
    }

    public void setIshabit(boolean ishabit) {
        Ishabit = ishabit;
    }

    public boolean isIspaid() {
        return Ispaid;
    }

    public void setIspaid(boolean ispaid) {
        Ispaid = ispaid;
    }

    public boolean isIskillerswitch() {
        return Iskillerswitch;
    }

    public void setIskillerswitch(boolean iskillerswitch) {
        Iskillerswitch = iskillerswitch;
    }

    public boolean isIscollection() {
        return Iscollection;
    }

    public void setIscollection(boolean iscollection) {
        Iscollection = iscollection;
    }

    public boolean isIssigned() {
        return Issigned;
    }

    public void setIssigned(boolean issigned) {
        Issigned = issigned;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
