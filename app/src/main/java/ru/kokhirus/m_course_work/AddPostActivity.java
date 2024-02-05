package ru.kokhirus.m_course_work;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

public class AddPostActivity extends AppCompatActivity {

    //private DatabaseReference mDatabase;
    private static final int REQUEST_CODE_IMAGE = 101;
    private ImageView addImageBtn;
    private EditText editText;
    private ProgressBar progressBar;
    private Button uploadBtn;
    Uri imageUri;
    boolean isImageAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Globals.database = FirebaseDatabase.getInstance().getReference();

        addImageBtn = findViewById(R.id.addImageBtn);
        editText = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar);
        uploadBtn = findViewById(R.id.uploadBtn);

        progressBar.setVisibility(View.GONE);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.database.child("postsCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            int postsCount = 0;
                            try {
                                postsCount = ((Long) task.getResult().getValue()).intValue();
                            }
                            catch (NullPointerException e)
                            {
                                Globals.database.child("postsCount").setValue(0);
                            }
                            if (isImageAdded) {
                                //mDatabase = FirebaseDatabase.getInstance().getReference().child("postsCount");
                                //Globals.postImageStorage = FirebaseStorage.getInstance().getReference().child("images");
                                uploadImage(postsCount);
                            }
                        }
                    }
                });
                //final
            }
        });

    }

    private void uploadImage(final int postsCount) {
        Globals.database.child("postsCount").setValue(postsCount+1);
        progressBar.setVisibility(View.VISIBLE);

        Globals.postImageStorage.child(postsCount+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Globals.postImageStorage.child(postsCount+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Post post = new Post(uri.toString(), postsCount, String.valueOf(editText.getText()), Globals.USER_DISPLAY_NAME, Globals.USER_UID);
//                        HashMap hashMap = new HashMap();
//                        hashMap.put("imageName", imageCounter);
//                        hashMap.put("imageUrl", uri.toString());
//                        hashMap.put("text", String.valueOf(editText.getText()));

                        //mDatabase = FirebaseDatabase.getInstance().getReference();


                        //database = FirebaseDatabase.getInstance().getReference();

                        Globals.postsDatabase.child(String.valueOf(postsCount)).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddPostActivity.this, "Data successfully uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                        //there was this peace of code before instead of the small one above
//                        Globals.database.child("postsCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                if (!task.isSuccessful()) {
//                                    Log.e("firebase", "Error getting data", task.getException());
//                                }
//                                else {
//                                    String postCount = String.valueOf(task.getResult().getValue());
//                                    if (postCount.equals("null")) {
//                                        postCount = "0";
//                                    }
//                                    Globals.database.child("posts").child(postCount).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            Toast.makeText(AddPostActivity.this, "Data successfully uploaded", Toast.LENGTH_SHORT).show();
//                                            finish();
//                                        }
//                                    });
//                                }
//                            }
//                        });

                        Globals.userDatabase.child("postsCount").setValue(++Globals.userPostsCount);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (snapshot.getBytesTransferred()*100)/snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && data != null) {
            imageUri = data.getData();
            isImageAdded = true;
            addImageBtn.setImageURI(imageUri);
        }
    }
}