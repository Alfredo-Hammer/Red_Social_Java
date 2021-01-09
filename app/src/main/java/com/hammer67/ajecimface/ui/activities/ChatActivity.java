package com.hammer67.ajecimface.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.adapters.MessageAdapter;
import com.hammer67.ajecimface.adapters.MyPostsAdapter;
import com.hammer67.ajecimface.models.Chat;
import com.hammer67.ajecimface.models.Message;
import com.hammer67.ajecimface.models.Post;
import com.hammer67.ajecimface.provider.AuthProvider;
import com.hammer67.ajecimface.provider.ChatsProvider;
import com.hammer67.ajecimface.provider.MessageProvider;
import com.hammer67.ajecimface.provider.UserProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    Toolbar mToolbar;
    View mActionBarview;

    ChatsProvider mChatsProvider;
    MessageProvider mMessageProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    ImageView mImageViewSendMessage, imageViewBack;
    CircleImageView mCircleImageViewProfileChat;
    EditText mEditTextMessage;
    TextView mTextViewUserName;
    TextView mTextViewRelativeTime;
    RecyclerView recyclerViewMessage;

    MessageAdapter mAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mImageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        mEditTextMessage = findViewById(R.id.editTextMessage);
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        mChatsProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

         linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
         linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custom_chat_toolbar);

        mImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkIfChatExist();
    }

    @Override
    public void onStart() {
        super.onStart();
      if (mAdapter != null){
          mAdapter.startListening();
      }

    }

    private void getMessageChat(){
        Query query = mMessageProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions
                .Builder<Message>().setQuery(query,Message.class)
                .build();

        mAdapter = new MessageAdapter(options,ChatActivity.this);
        recyclerViewMessage.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViwed();
                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage -1) && lastMessagePosition == positionStart -1)){
                    recyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void sendMessage() {
        String textMessage = mEditTextMessage.getText().toString();
        if (!textMessage.isEmpty()) {
            Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            } else {
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textMessage);

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mEditTextMessage.setText("");
                        mAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(ChatActivity.this, "Error!! no se envio el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarview = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarview);

        mCircleImageViewProfileChat = mActionBarview.findViewById(R.id.circleImageViewProfile);
        mTextViewUserName = mActionBarview.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarview.findViewById(R.id.textViewRelativeTime);
        imageViewBack = mActionBarview.findViewById(R.id.imageViewBack);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserInfo();
    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idUserInfo = mExtraIdUser2;
        } else {
            idUserInfo = mExtraIdUser1;
        }
        mUserProvider.getUser(idUserInfo).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("userName")) {
                        String username = documentSnapshot.getString("userName");
                        mTextViewUserName.setText(username);
                    }

                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfil = documentSnapshot.getString("image_profile");
                        if (imageProfil != null){
                            if (!imageProfil.equals("")){
                                Picasso.with(ChatActivity.this).load(imageProfil).into(mCircleImageViewProfileChat);
                            }
                        }
                    }
                }
            }
        });
    }

    private void checkIfChatExist() {
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                }
                else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMessageChat();
                    updateViwed();
                }
            }
        });
    }

    private void updateViwed() {
        String idSender = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        }
        else {
            idSender = mExtraIdUser1;

        }
        mMessageProvider.getMessageByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    mMessageProvider.updateViewed(documentSnapshot.getId(),true);
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }
}