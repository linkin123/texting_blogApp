package com.example.texting.common.model.dataAccess;

import com.example.texting.common.Constants;
import com.example.texting.common.model.pojo.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRealtimeDatabaseAPI {
    public static final String SEPARATOR = "___&___";
    public static final String PATH_USERS = "users";
    public static final String PATH_CONTACTS = "contacts";
    public static final String PATH_REQUEST = "requests";

    private DatabaseReference mDatabaseReference;



    private static class SingletonHolder{
        private static final FirebaseRealtimeDatabaseAPI INSTANCE = new FirebaseRealtimeDatabaseAPI();
    }

    public static FirebaseRealtimeDatabaseAPI getInstance(){
        return  SingletonHolder.INSTANCE;
    }

    private FirebaseRealtimeDatabaseAPI(){
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /*
    * References
    *
    * */
    public DatabaseReference getRootReference(){
        return mDatabaseReference.getRoot();
    }

    public DatabaseReference getUserReferenceByUid(String uid){
        return getRootReference().child(PATH_USERS).child(uid);
    }

    public DatabaseReference getContactsReference(String uid) {
        return getUserReferenceByUid(uid).child(PATH_CONTACTS);
    }

    public DatabaseReference getRequestReference(String email) {
        return getRootReference().child(PATH_REQUEST).child(email);
    }

    public void updateMyLastConnection(boolean online, String uid) {
        updateMyLastConnection(online, "" , uid);
    }

    public void updateMyLastConnection(Boolean online, String uidFriend, String uid){
        String lastConnectionWIth = Constants.ONLINE_VALUE + SEPARATOR + uidFriend;
        Map<String , Object> values = new HashMap<>();
        values.put(User.LAST_CONNECTION_WITH , online? lastConnectionWIth: ServerValue.TIMESTAMP);

        //offline
        getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        getUserReferenceByUid(uid).updateChildren(values);

        if(online){
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect()
                    .setValue(ServerValue.TIMESTAMP);
        }else{
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect().cancel();
        }
    }



}
