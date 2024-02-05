package ru.kokhirus.m_course_work;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    SignInButton googleAuth;
    FirebaseAuth auth;
    //FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        googleAuth = findViewById(R.id.googleAuthBtn);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Globals.USER_UID = user.getUid();
            Globals.userDatabase = Globals.database.child("users").child(Globals.USER_UID);
            Globals.favDatabase = Globals.database.child("usersFavorite").child(Globals.USER_UID);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


        //database = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn() {

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

//                            Globals.database = FirebaseDatabase.getInstance().getReference();
                            //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                            Globals.USER_UID = user.getUid();
                            Globals.userDatabase = Globals.database.child("users").child(Globals.USER_UID);
                            Globals.favDatabase = Globals.database.child("usersFavorite").child(Globals.USER_UID);

                            Globals.userDatabase.child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        if (!task.getResult().exists()) {
                                            HashMap<String,Object> map = new HashMap<>();
                                            map.put("id", user.getUid());
                                            map.put("name", user.getDisplayName());
                                            map.put("profileImage", user.getPhotoUrl().toString());

                                            Globals.userDatabase.setValue(map);
                                        }
                                    }
                                }
                            });


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}