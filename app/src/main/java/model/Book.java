package model;

public class Book {

    String Bookname, Noofpages,  Pagesread, Bookid;

    public Book() {
    }


    public Book(String bookname, String noofpages, String pagesread, String bookid) {
        Bookname = bookname;
        Noofpages = noofpages;
        Pagesread = pagesread;
        Bookid = bookid;
    }

    public String getBookname() {
        return Bookname;
    }

    public void setBookname(String bookname) {
        Bookname = bookname;
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
}
