package com.hammer67.ajecimface.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.models.Chat;
import com.hammer67.ajecimface.models.Coments;
import com.hammer67.ajecimface.provider.AuthProvider;
import com.hammer67.ajecimface.provider.UserProvider;
import com.hammer67.ajecimface.ui.activities.ChatActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    public ChatsAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String chatId = document.getId();
        if (mAuthProvider.getUid().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(), holder);
        }
        else {
            getUserInfo(chat.getIdUser1(), holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chatId,chat.getIdUser1(),chat.getIdUser2());
            }
        });

    }

    private void goToChatActivity(String chatId,String idUser1, String idUser2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat",chatId);
        intent.putExtra("idUser1",idUser1);
        intent.putExtra("idUser2",idUser2);
        context.startActivity(intent);

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
                                Picasso.with(context).load(imageProfile).into(holder.mCircleImageChat);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName, textViewLastMessage;
        CircleImageView mCircleImageChat;
        View viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUserName = itemView.findViewById(R.id.textViewUsernameChat);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessageChat);
            mCircleImageChat = itemView.findViewById(R.id.circleImageChat);
            viewHolder = itemView;
        }
    }
}
