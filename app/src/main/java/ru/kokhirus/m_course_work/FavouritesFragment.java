package ru.kokhirus.m_course_work;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;


public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter mAdapter;
    private List<Post> favList = new ArrayList<Post>();
    public MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.postList);

        initialization();



//        Globals.postsDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    //Log.i("getData", ds.getValue().toString());
//                    if (list.size() > ds.getValue(Post.class).getPostID()) continue;
//                    Post post = ds.getValue(Post.class);
//                    if (post == null) {
//                        Toast
//                                .makeText(mainActivity, "Received empty post",
//                                        Toast.LENGTH_SHORT)
//                                .show();
//                        continue;
//                    }
//
//                    list.add(post);
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
        return view;
    }


    public void getDataFromDB() {
        if (favList.size() > 0)
            favList.clear();
        for (Post post : mainActivity.list) {
            Globals.favDatabase.child(String.valueOf(post.getPostID())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        if (task.getResult().getValue() != null)
                        {
                            favList.add(post);
                        }

//                        if (list.size() == 0)
//                        {
//                            warning_text.setVisibility(View.VISIBLE);
//                            warning_image.setVisibility(View.VISIBLE);
//                            try {
//                                Glide.with(FavoriteActivity.this).load(Constants.ERROR_IMAGE).into(warning_image);
//                            }
//                            catch(Exception e)
//                            {
//                            }
//                        }
//                        else
//                        {
//                            warning_text.setVisibility(View.INVISIBLE);
//                            warning_image.setVisibility(View.INVISIBLE);
//                        }
                        mAdapter.setList(favList);
                    }
                }
            });
        }
    }

    private void initialization() {
        mainActivity = (MainActivity) getActivity();
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = mainActivity.mAdapter;
        getDataFromDB();

//        mAdapter = new PostAdapter(mainActivity, list, new PostAdapter.OnPostListener() {
//
//            @Override
//            public void onPostClick(int pos) {}
//
//            @Override
//            public void onFavoriteClick(int pos) {
//                Log.i("APP", "OnFavorite");
//                Post item = list.get(pos);
//                String strPostID = String.valueOf(item.getPostID());
//                Globals.favDatabase.child(strPostID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        if (task.isSuccessful())
//                        {
//                            if (task.getResult().exists())
//                            {
//                                Globals.favDatabase.child(strPostID).setValue(null);
//                            }
//                            else
//                            {
//                                Globals.favDatabase.child(strPostID).setValue(true);
//                            }
//                        }
//                    }
//                });
//            }
//        });

        recyclerView.setAdapter(mAdapter);
    }
}