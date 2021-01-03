package com.hammer67.ajecimface.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.models.Coments;
import com.hammer67.ajecimface.models.Post;
import com.hammer67.ajecimface.provider.UserProvider;
import com.hammer67.ajecimface.ui.activities.PostDetailActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentsAdapter extends FirestoreRecyclerAdapter<Coments, ComentsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;

    public ComentsAdapter(@NonNull FirestoreRecyclerOptions<Coments> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Coments coments) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String comentId = document.getId();
        String idUser = document.getString("idUser");
        holder.textViewComents.setText(coments.getComents());
        getUserInfo(idUser, holder);


    }

    private void getUserInfo(String idUser, ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("userName")) {
                        String userName = documentSnapshot.getString("userName");
                        holder.textViewUserName.setText(userName);
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.mCircleImageViewComent);
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_coments, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName, textViewComents;
        CircleImageView mCircleImageViewComent;
        View viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUserName = itemView.findViewById(R.id.textViewUsername);
            textViewComents = itemView.findViewById(R.id.textViewComents);
            mCircleImageViewComent = itemView.findViewById(R.id.circleImageViewComent);
            viewHolder = itemView;
        }
    }
}
