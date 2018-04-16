package com.example.malicteam.projectxclient.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maayan on 11-Apr-18.
 */

public class CloudManager {



    //Interfaces
    public interface CloudCallback<T> {
        void onComplete(T data);

        void onCancel();
    }

//    public static void getFriendAndObserve(long lastUpdate, final CloudCallback<List<User>> callback) {
//        Log.d("TAG", "getFriendsFromCloud " + lastUpdate);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("employees");
//        Query query = myRef.orderByChild("lastUpdated").startAt(lastUpdate);
//        ValueEventListener listener = query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<User> list = new LinkedList<User>();
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    User user = snap.getValue(User.class);
//                    list.add(user);
//                }
//                callback.onComplete(list);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                callback.onComplete(null);
//            }
//        });
//    }

}
