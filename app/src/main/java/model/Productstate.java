package model;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;

public class Productstate {

    private static ArrayList<Productstate> languageArrayList;
    int id;
    String name;


    public Productstate(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void iniatializeLang(){
        languageArrayList = new ArrayList<>();
        Productstate launched = new Productstate(0, "Today");
        languageArrayList.add(launched);

        Productstate notLaunched = new Productstate(1, "Tomorrow");
        languageArrayList.add(notLaunched);
    }

    public static ArrayList<Productstate> getLanguageArrayList() {
        return languageArrayList;
    }

    public static String[] state(){
        String[] names = new String[languageArrayList.size()];

        for (int i =0; i<languageArrayList.size(); i++){
            names[i] = languageArrayList.get(i).name;
        }

        return names;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
