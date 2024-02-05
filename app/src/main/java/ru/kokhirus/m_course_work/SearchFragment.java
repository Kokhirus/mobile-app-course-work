package ru.kokhirus.m_course_work;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter mAdapter;
    private List<Post> list;
    public MainActivity mainActivity;
    private SearchView searchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
        recyclerView = view.findViewById(R.id.postList);



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

        return view;
    }

    private void filterList(String text) {
        List<Post> filteredList = new ArrayList<>();
        for (Post post : list) {
            if (post.getText().toLowerCase().contains(text.toLowerCase())
                    || post.getUserName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(post);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("FilterList", "\nFiltered list postIDs:");
            for (Post p : filteredList)
                Log.i("FilterList", p.getPostID()+"");
            mAdapter.setList(filteredList);
        }
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

        list = mainActivity.list;

        mAdapter = mainActivity.mAdapter;
        recyclerView.setAdapter(mAdapter);
    }

}