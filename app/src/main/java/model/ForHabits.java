package model;

public class ForHabits {

    String Counter, Item, Lastchecked, Timestamp, Userid, Noofpages,  Pagesread, Bookid;

    boolean Checked, Isjournal, Isbook;

    public ForHabits() {
    }


    public ForHabits(String counter, String item, String lastchecked, String timestamp, String userid, String noofpages, String pagesread, String bookid, boolean checked, boolean isjournal, boolean isbook) {
        Counter = counter;
        Item = item;
        Lastchecked = lastchecked;
        Timestamp = timestamp;
        Userid = userid;
        Noofpages = noofpages;
        Pagesread = pagesread;
        Bookid = bookid;
        Checked = checked;
        Isjournal = isjournal;
        Isbook = isbook;
    }

    public String getCounter() {
        return Counter;
    }

    public void setCounter(String counter) {
        Counter = counter;
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String item) {
        Item = item;
    }

    public String getLastchecked() {
        return Lastchecked;
    }

    public void setLastchecked(String lastchecked) {
        Lastchecked = lastchecked;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getNoofpages() {
        return Noofpages;
    }

    public void setNoofpages(String noofpages) {
        Noofpages = noofpages;
    }

    public String getPagesread() {
        return Pagesread;
    }

    public void setPagesread(String pagesread) {
        Pagesread = pagesread;
    }

    public String getBookid() {
        return Bookid;
    }

    public void setBookid(String bookid) {
        Bookid = bookid;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public boolean isIsjournal() {
        return Isjournal;
    }

    public void setIsjournal(boolean isjournal) {
        Isjournal = isjournal;
    }

    public boolean isIsbook() {
        return Isbook;
    }

    public void setIsbook(boolean isbook) {
        Isbook = isbook;
    }
}
