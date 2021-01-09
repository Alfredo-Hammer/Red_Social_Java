package com.hammer67.ajecimface.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.models.Like;
import com.hammer67.ajecimface.models.Post;
import com.hammer67.ajecimface.provider.AuthProvider;
import com.hammer67.ajecimface.provider.LikesProvider;
import com.hammer67.ajecimface.provider.PostProvider;
import com.hammer67.ajecimface.provider.UserProvider;
import com.hammer67.ajecimface.ui.activities.PostDetailActivity;
import com.hammer67.ajecimface.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;


    public MyPostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;

        mUserProvider = new UserProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();

        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);
        holder.textViewRelativeTime.setText(relativeTime);
        holder.textViewTitle.setText(post.getTitle());

        if (post.getIdUser().equals(mAuthProvider.getUid())){
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageViewDelete.setVisibility(View.GONE);

        }

        if (post.getImage1() != null) {
            if (!post.getImage1().isEmpty()) {
                Picasso.with(context).load(post.getImage1()).into(holder.circleImageViewPost);
            }
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showConfirmDelete(postId);
            }
        });

    }

    private void showConfirmDelete(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_baseline_alert);
        builder.setTitle("Eliminar una publicacion");
        builder.setMessage("Estas seguro de eliminar el post?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                })

                .setNegativeButton("No", null)
                .show();


    }

    private void deletePost(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toasty.success(context, "El post se elimino correctamente", Toasty.LENGTH_SHORT).show();
                } else {
                    Toasty.error(context, "No se pudo eliminar el post, intente mas tarde", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewRelativeTime;
        CircleImageView circleImageViewPost;
        ImageView imageViewDelete;
        View viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewUserTitleMyPost);
            textViewRelativeTime = itemView.findViewById(R.id.textViewRelativeTimeMyPost);
            circleImageViewPost = itemView.findViewById(R.id.circleImageMyPost);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeleteMyPost);

            viewHolder = itemView;
        }
    }
}
