package com.hammer67.ajecimface.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.models.Chat;
import com.hammer67.ajecimface.models.Message;
import com.hammer67.ajecimface.provider.AuthProvider;
import com.hammer67.ajecimface.provider.UserProvider;
import com.hammer67.ajecimface.ui.activities.ChatActivity;
import com.hammer67.ajecimface.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String messageId = document.getId();
        holder.textViewMessage.setText(message.getMessage());

        String relativetime = RelativeTime.timeFormatAMPM(message.getTimestamp(),context);
        holder.textViewDate.setText(relativetime);

        if (message.getIdSender().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0,0,0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,25,20,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.linearlayout_backround));
            holder.mImageViewCheck.setVisibility(View.VISIBLE);
        }
        else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0,0,0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,25,30,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.linearlayout_backround_grey));
            holder.mImageViewCheck.setVisibility(View.GONE);
            holder.textViewMessage.setTextColor(Color.DKGRAY);
            holder.textViewDate.setTextColor(Color.GRAY);

        }

        if (message.isViewed()){
            holder.mImageViewCheck.setImageResource(R.drawable.check_blue);
        }
        else{
            holder.mImageViewCheck.setImageResource(R.drawable.check_grey);

        }

    }

    private void getUserImageProfile(String idUser,ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(context).load(imageProfile).into(holder.mCircleImageProfile);
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage, textViewDate;
        CircleImageView mCircleImageProfile;
        ImageView mImageViewCheck;
        LinearLayout linearLayoutMessage;
        View viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            mCircleImageProfile = itemView.findViewById(R.id.circleImageProfileMessage);
            mImageViewCheck = itemView.findViewById(R.id.imageViewCheck);
            linearLayoutMessage = itemView.findViewById(R.id.linearLayoutMessage);
            viewHolder = itemView;
        }
    }
}
