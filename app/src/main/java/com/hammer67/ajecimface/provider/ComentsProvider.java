package com.hammer67.ajecimface.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hammer67.ajecimface.models.Coments;

public class ComentsProvider {

    CollectionReference mCollection;

    public ComentsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("coments");
    }

    public Task<Void> create(Coments coments){
        return mCollection.document().set(coments);
    }

    public Query getComentsByPost(String idPost){
        return mCollection.whereEqualTo("idPost",idPost);
    }


}
