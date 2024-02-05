package ru.kokhirus.m_course_work;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private OnPostListener onPostListener;
    private Activity activity;
    private List<Post> list;

    /** Adapter default constructor */
    public PostAdapter(Activity activity, List<Post> list,
                         OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
        this.list = list;
        this.activity = activity;
    }

//    /** onCreate method overrride for ViewHolder */
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.view_post, parent, false);
//        return new ViewHolder(itemView, onPostListener);
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(itemView, onPostListener);
    }

    /** ViewHolder class */
    public class ViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        ConstraintLayout constraintLayout;
        TextView usernameView, textView;

        ShapeableImageView avatarView;
        ImageView imageView;
        MaterialCheckBox favoritesButton;

        OnPostListener onPostListener;



        /** Viewholder constructor */
        public ViewHolder(View view, OnPostListener onPostListener) {
            super(view);
            constraintLayout = view.findViewById(R.id.postLayout);

            usernameView = view.findViewById(R.id.usernameView);
            avatarView = view.findViewById(R.id.avatarView);
            textView = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView);
            favoritesButton = view.findViewById(R.id.favoriteButton);

            this.onPostListener = onPostListener;

            favoritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPostListener != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onFavoriteClick(pos);
                        }
                    }
                }
            });

            view.setOnClickListener(this);
        }
        /** onClick listener for each post */
        @Override
        public void onClick(View v) {
            onPostListener.onPostClick(getAbsoluteAdapterPosition());
        }
    }

    /** ViewHolder 'update' method */
    public void onBindViewHolder(ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Post item = list.get(position);

        holder.usernameView.setText(item.getUserName());
        holder.usernameView.setTextSize(18);
        holder.textView.setText(item.getText());
        holder.textView.setTextSize(16);

        Glide.with(activity).load(list.get(position).getImageUrl()).into(holder.imageView);

        Globals.database.child("users").child(item.getUserUid()).child("profileImage").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().exists())
                    {
                        Glide.with(activity).load(task.getResult().getValue()).into(holder.avatarView);

                    }
                    else
                    {
                        Log.e("APP", "Didn't find avatar (google, how)");
                        //holder.avatarView.setImageDrawable()
                    }
                }
            }
        });

        Globals.favDatabase.child(String.valueOf(item.getPostID())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().exists())
                    {
                        holder.favoritesButton.setChecked(true);
                    }
                    else
                    {
                        holder.favoritesButton.setChecked(false);
                    }
                }
                else
                {
                    Log.e("APP", "HELP ME PLEASE");
                }
            }
        });


    }

    public void setList(List<Post> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /** Helper method */
    public int getItemCount() { return list.size(); }

    public void onFavoriteClick(int pos) {
        Log.i("APP", "OnFavorite");
        Post post = list.get(pos);
        String strPostID = String.valueOf(post.getPostID());
        Globals.favDatabase.child(strPostID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().exists())
                    {
                        Globals.favDatabase.child(strPostID).setValue(null);
                    }
                    else
                    {
                        Globals.favDatabase.child(strPostID).setValue(true);
                    }
                }
            }
        });
    }

    /** Interface for handling clicks on each post card */
    public interface OnPostListener {
        void onPostClick(int pos);
        //void onButtonClick(int pos);
        //void onFavoriteClick(int pos);
    }



}
