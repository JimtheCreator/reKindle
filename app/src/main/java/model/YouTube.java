package model;

public class YouTube {
    String Name, Profilepic, Youtubedescription, Youtubelink;

    public YouTube() {
    }


    public YouTube(String name, String profilepic, String youtubedescription, String youtubelink) {
        Name = name;
        Profilepic = profilepic;
        Youtubedescription = youtubedescription;
        Youtubelink = youtubelink;
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

    public String getYoutubedescription() {
        return Youtubedescription;
    }

    public void setYoutubedescription(String youtubedescription) {
        Youtubedescription = youtubedescription;
    }

    public String getYoutubelink() {
        return Youtubelink;
    }

    public void setYoutubelink(String youtubelink) {
        Youtubelink = youtubelink;
    }
}
