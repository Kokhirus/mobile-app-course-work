package ru.kokhirus.m_course_work;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Globals {
    public static String USER_UID = "none";

    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference postsDatabase = database.child("posts");
    public static DatabaseReference userDatabase;
    public static DatabaseReference favDatabase;

    public static StorageReference postImageStorage = FirebaseStorage.getInstance().getReference().child("images");

    public static String USER_DISPLAY_NAME;
    public static int userPostsCount = 0;


}
