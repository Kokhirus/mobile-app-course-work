package ru.kokhirus.m_course_work;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter mAdapter;
    private FloatingActionButton newPostBtn;
    private List<Post> list;
    public MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.postList);



        initialization();


        Globals.postsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Log.i("getData", ds.getValue().toString());
                    if (list.size() > ds.getValue(Post.class).getPostID()) continue;
                    Post post = ds.getValue(Post.class);
                    if (post == null) {
                        Toast
                                .makeText(mainActivity, "Received empty post",
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
        });

        newPostBtn = (FloatingActionButton) view.findViewById(R.id.newPostBtn);
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });
        return view;
    }
    private void initialization() {
//        if (mainActivity != null) {
//            Log.i("NewsFragment", "mainactivity list got " + String.valueOf(mainActivity.list.size()));
//            Log.i("NewsFragment", "fragment list got " + String.valueOf(this.list.size()));
//        }
        mainActivity = (MainActivity) getActivity();
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = mainActivity.mAdapter;
        list = mainActivity.list;
        mAdapter.setList(list);

        recyclerView.setAdapter(mAdapter);
    }

}

