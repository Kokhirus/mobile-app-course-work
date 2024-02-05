package ru.kokhirus.m_course_work;

import java.util.HashMap;

public class Post {

    private int postID;
    private String userName;
    private String userUid;
    private String imageUrl;
    private String text;
    public Post(String imageUrl, int postID, String text, String userName, String userUid) {
        this.postID = postID;
        this.userName = userName;
        this.userUid = Globals.USER_UID;
        this.imageUrl = imageUrl;
        this.text = text;
    }

    public Post() {}

    public int getPostID() {
        return postID;
    }

    public String getUserName() {return userName;}

    public String getUserUid() {
        return userUid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }


}
