package com.hammer67.ajecimface.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.adapters.ComentsAdapter;
import com.hammer67.ajecimface.adapters.PostsAdapter;
import com.hammer67.ajecimface.adapters.SliderAdapter;
import com.hammer67.ajecimface.fragments.ProfileFragment;
import com.hammer67.ajecimface.models.Coments;
import com.hammer67.ajecimface.models.FCMBody;
import com.hammer67.ajecimface.models.FCMResponse;
import com.hammer67.ajecimface.models.Post;
import com.hammer67.ajecimface.models.SliderItem;
import com.hammer67.ajecimface.provider.AuthProvider;
import com.hammer67.ajecimface.provider.ComentsProvider;
import com.hammer67.ajecimface.provider.NotificationProvider;
import com.hammer67.ajecimface.provider.PostProvider;
import com.hammer67.ajecimface.provider.TokenProvider;
import com.hammer67.ajecimface.provider.UserProvider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    PostProvider mPostProvider;
    UserProvider mUserProvider;
    ComentsProvider mComentsProvider;
    AuthProvider mAuthProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    ComentsAdapter mAdapter;

    String mPostExtraId;
    TextView mTextViewUserName;
    TextView mTextViewTitle;
    TextView mTextViewShowProfile;
    TextView mTextViewDescription;
    TextView mTextViewPhone;
    TextView mTextViewNameCategory;
    ImageView mImageViewCategory;
    CircleImageView circleImageViewProfile;
    FloatingActionButton mFabComents;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    String mIdUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView = findViewById(R.id.imageSlider);
        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewNameCategory = findViewById(R.id.textViewNameCategory);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        circleImageViewProfile = findViewById(R.id.circleImageProfile);
        mTextViewShowProfile = findViewById(R.id.textViewShowProfile);
        mTextViewUserName = findViewById(R.id.textViewUsername);
        mFabComents = findViewById(R.id.fabComents);
        mRecyclerView = findViewById(R.id.recyclerViewComent);
        mToolbar = findViewById(R.id.toolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mComentsProvider = new ComentsProvider();
        mAuthProvider = new AuthProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mPostExtraId = getIntent().getStringExtra("id");

        mFabComents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComents();
            }
        });

        mTextViewShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });

        getPost();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mComentsProvider.getComentsByPost(mPostExtraId);
        FirestoreRecyclerOptions<Coments> options = new FirestoreRecyclerOptions.Builder<Coments>().setQuery(query, Coments.class).build();

        mAdapter = new ComentsAdapter(options, PostDetailActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void showDialogComents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡¡COMENTARIO!!");
        builder.setMessage("Escribir su comentario");

        EditText editText = new EditText(this);
        editText.setHint("Texto...");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36, 0, 36, 36);
        editText.setLayoutParams(params);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        relativeLayout.setLayoutParams(relativeParams);
        relativeLayout.addView(editText);

        builder.setView(relativeLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                if (!value.isEmpty()) {
                    createComent(value);
                } else {
                    Toasty.warning(PostDetailActivity.this, "Debes escribir un comentario", Toasty.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void createComent(String value) {
        Coments coments = new Coments();
        coments.setComents(value);
        coments.setIdPost(mPostExtraId);
        coments.setIdUser(mAuthProvider.getUid());
        coments.setTimestamp(new Date().getTime());

        mComentsProvider.create(coments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNotification(value);
                    Toasty.success(PostDetailActivity.this, "Comentario publicado con exito", Toasty.LENGTH_SHORT).show();
                } else {
                    Toasty.error(PostDetailActivity.this, "No se pudo publicar el comentario", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(String coment) {
        if (mIdUser != null) {
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO");
                        data.put("body", coment);
                        FCMBody body = new FCMBody(token, "high", "4500", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getSuccess() == 1) {
                                        Toasty.success(PostDetailActivity.this, "La notificacion se envio correctamente", Toasty.LENGTH_SHORT).show();
                                    } else {
                                        Toasty.error(PostDetailActivity.this, "La notificacion no se envio correctamente", Toasty.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toasty.error(PostDetailActivity.this, "La notificacion no se envio correctamente", Toasty.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Toasty.error(PostDetailActivity.this, "Token de notificaciones del usuario no existe", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        } else {
            Toasty.info(PostDetailActivity.this, "La identificacion del usuario aun no se carga...", Toasty.LENGTH_SHORT).show();
        }
    }

    private void instanceSlider() {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.YELLOW);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(2);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost() {
        mPostProvider.getPostById(mPostExtraId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("image1")) {
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("image2")) {
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        mTextViewTitle.setText(title.toUpperCase());
                    }
                    if (documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        mTextViewDescription.setText(description);
                    }
                    if (documentSnapshot.contains("category")) {
                        String category = documentSnapshot.getString("category");
                        mTextViewNameCategory.setText(category);

                        if (category.equals("Iglesia Nicaragua")) {
                            mImageViewCategory.setImageResource(R.drawable.nicaragua);

                        } else if (category.equals("Iglesia Panama")) {
                            mImageViewCategory.setImageResource(R.drawable.panama);

                        } else if (category.equals("Iglesia Costa Rica")) {
                            mImageViewCategory.setImageResource(R.drawable.nicaragua);

                        } else if (category.equals("Iglesia Honduras")) {
                            mImageViewCategory.setImageResource(R.drawable.panama);

                        } else if (category.equals("Iglesia Estados Unidos")) {
                            mImageViewCategory.setImageResource(R.drawable.nicaragua);

                        } else if (category.equals("Iglesia Suecia")) {
                            mImageViewCategory.setImageResource(R.drawable.nicaragua);
                        }
                    }
                    if (documentSnapshot.contains("idUser")) {
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }

                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("userName")) {
                        String userName = documentSnapshot.getString("userName");
                        mTextViewUserName.setText(userName);
                    }
                    if (documentSnapshot.contains("phone")) {
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(circleImageViewProfile);
                    }
                }
            }
        });
    }
}