package com.hammer67.ajecimface.provider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hammer67.ajecimface.models.Token;

public class TokenProvider {

    CollectionReference mCollection;

    public TokenProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Token");
    }

    public void create(String idUser){
        if (idUser == null){
            return;
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token token = new Token(instanceIdResult.getToken());
                mCollection.document(idUser).set(token);
            }
        });
    }

    public Task<DocumentSnapshot> getToken(String idUser){
        return mCollection.document(idUser).get();
    }
}