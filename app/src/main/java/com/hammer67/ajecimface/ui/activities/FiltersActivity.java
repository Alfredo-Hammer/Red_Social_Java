package com.hammer67.ajecimface.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.hammer67.ajecimface.R;
import com.hammer67.ajecimface.adapters.PostsAdapter;
import com.hammer67.ajecimface.models.Post;
import com.hammer67.ajecimface.provider.AuthProvider;
import com.hammer67.ajecimface.provider.PostProvider;

import es.dmoral.toasty.Toasty;

public class FiltersActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TextView mTextViewNumberFilter;
    String mExtraCategory;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilter);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }


    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post.class).build();

        mPostsAdapter = new PostsAdapter(options,FiltersActivity.this,mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostsAdapter);
        mRecyclerView.setHasFixedSize(true);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return true;
    }
}