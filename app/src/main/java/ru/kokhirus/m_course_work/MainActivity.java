package ru.kokhirus.m_course_work;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    //DatabaseReference database;
    FavouritesFragment favouritesFragment = new FavouritesFragment();
    NewsFragment newsFragment = new NewsFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SearchFragment searchFragment = new SearchFragment();
    public LinearLayoutManager layoutManager;
    public PostAdapter mAdapter;
    public List<Post> list = new ArrayList<Post>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Globals.USER_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Globals.usersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(Globals.USER_UID);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initialization();
        getDataFromDB();
        //Toast.makeText(MainActivity.this, "USER_UID: " + Globals.USER_UID, Toast.LENGTH_SHORT).show();
        Globals.userDatabase.child("postsCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    if (task.getResult().exists()) {
                        String tmp = task.getResult().getValue().toString();
                        Globals.userPostsCount = Integer.parseInt(tmp);//task.getResult().getValue().toString());
                    }
                    else {
                        Globals.userDatabase.child("postsCount").setValue(0);
                    }
                }
//                if (String.valueOf(task.getResult().getValue()).equals("")) {
//                    mDatabase.child("users").child(Globals.USER_UID).child("posts").setValue(0);
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    Globals.POSTS_COUNT = Integer.parseInt(String.valueOf(task.getResult().getValue()));
//                }
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out).replace(R.id.container, profileFragment).commit();
                    return true;
                } else if (itemId == R.id.news) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out).replace(R.id.container, newsFragment).commit();
                    return true;
                } else if (itemId == R.id.favourites) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out).replace(R.id.container, favouritesFragment).commit();
                    return true;
                } else if (itemId == R.id.search) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out).replace(R.id.container, searchFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    private void initialization() {
        mAdapter = new PostAdapter(this, list, new PostAdapter.OnPostListener() {
            @Override
            public void onPostClick(int pos) {}

        });
    }
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list.size() > 0)
                    list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Log.i("getData", ds.getValue().toString());
                    Post post = ds.getValue(Post.class);
                    if (post == null) {
                        Toast
                                .makeText(MainActivity.this, "Received empty post",
                                        Toast.LENGTH_SHORT)
                                .show();
                        continue;
                    }

                    list.add(post);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        Globals.postsDatabase.addValueEventListener(vListener);
    }
}