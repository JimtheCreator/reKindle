package model;

import java.util.List;

public class KeyClass {

    String getKey;
    List<Journal> journalList;

    public KeyClass(String getKey, List<Journal> journalList) {
        this.getKey = getKey;
        this.journalList = journalList;
    }

    public KeyClass() {
    }

    public String getGetKey() {
        return getKey;
    }

    public void setGetKey(String getKey) {
        this.getKey = getKey;
    }

    public List<Journal> getJournalList() {
        return journalList;
    }

    public void setJournalList(List<Journal> journalList) {
        this.journalList = journalList;
    }
}
