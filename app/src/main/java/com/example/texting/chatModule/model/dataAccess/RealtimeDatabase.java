package com.example.texting.chatModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.texting.R;
import com.example.texting.chatModule.model.LastConnectionEventListener;
import com.example.texting.chatModule.model.MessagesEventListener;
import com.example.texting.chatModule.model.SendMessageListener;
import com.example.texting.common.Constants;
import com.example.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.example.texting.common.model.pojo.Message;
import com.example.texting.common.model.pojo.User;
import com.example.texting.common.model.utils.UtilsCommon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabase {

    private static final String PATH_CHATS = "chats";
    private static final String PATH_MESSAGES = "messages";

    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;
    private ChildEventListener mMessageEventListener;
    private ValueEventListener mFriendProfileListener;

    public RealtimeDatabase(){
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI(){
        return mDatabaseAPI;
    }

    public void subscribeToMessages(String myEmail, String friendEmail, final MessagesEventListener listener){
        if(mMessageEventListener == null){
            mMessageEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onMessageReceived(getMessage(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    switch (databaseError.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.chat_error_permission_denied);
                            break;
                            default:
                                listener.onError(R.string.common_error_server);
                                break;
                    }
                }
            };
        }

        getChatsMessagesReference(myEmail, friendEmail).addChildEventListener(mMessageEventListener);

    }

    private DatabaseReference getChatsMessagesReference(String myEmail, String friendEmail) {
        return getChatsReference(myEmail , friendEmail).child(PATH_MESSAGES);
    }

    private DatabaseReference getChatsReference(String myEmail, String friendEmail) {
        String myEmailEncoded = UtilsCommon.getEmailEncoded(myEmail);
        String friendEmailEncoded = UtilsCommon.getEmailEncoded(friendEmail);

        String keyChat = myEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + friendEmailEncoded;

        if(myEmailEncoded.compareTo(friendEmailEncoded) > 0){
            keyChat = friendEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + myEmailEncoded;
        }

        return mDatabaseAPI.getRootReference().child(PATH_CHATS).child(keyChat);
    }

    private Message getMessage(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        if(message != null){
            message.setUid(dataSnapshot.getKey());
        }
        return message;
    }

    public void unsubscribeToMessage(String myEmail, String friendEmail){
        if(mMessageEventListener != null){
            getChatsMessagesReference(myEmail, friendEmail).removeEventListener(mMessageEventListener);
        }
    }

    public void subscribeToFriend(String uid, final LastConnectionEventListener listener){
        if(mFriendProfileListener == null){
            mFriendProfileListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long lastConnectionFriend = 0;
                    String uidConnectedFriend = "";
                    try{
                        Long value = dataSnapshot.getValue(Long.class);
                        if(value != null){
                            lastConnectionFriend = value;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        String lastConnectionWith = dataSnapshot.getValue(String.class);
                        if(lastConnectionWith != null && !lastConnectionWith.isEmpty()){
                            String[] values = lastConnectionWith.split(FirebaseRealtimeDatabaseAPI.SEPARATOR);
                            if(values.length > 0){
                                lastConnectionFriend = Long.valueOf(values[0]);
                                if(values.length > 1){
                                    uidConnectedFriend = values[1];
                                }
                            }
                        }
                    }
                    listener.onSuccess(lastConnectionFriend == Constants.ONLINE_VALUE,
                            lastConnectionFriend, uidConnectedFriend);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };
        }
        //offline
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH)
                .addValueEventListener(mFriendProfileListener);
    }

    public void unsubscribeToFriend(String uid){
        if(mFriendProfileListener != null){
            mDatabaseAPI.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH)
                    .removeEventListener(mFriendProfileListener);
        }
    }

    /*
    * read/unread messages
    * */

    public void setMessageRead(String myUid , String friendUid){
        final DatabaseReference userReference = getOneContactReference(myUid , friendUid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null){
                    Map<String , Object> updates = new HashMap<>();
                    updates.put(User.MESSAGE_UNREAD, 0);
                    userReference.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference getOneContactReference(String uidMain, String uidChild) {
        return mDatabaseAPI.getUserReferenceByUid(uidMain).child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS)
                .child(uidChild);
    }

    public void sumUnreadMessages(String myUid, String friendUid){
        final DatabaseReference userReference = getOneContactReference(friendUid, myUid);

        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if(user == null){
                    return Transaction.success(mutableData);
                }

                user.setMessagesUnread(user.getMessagesUnread() + 1);
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    /*
    * send message
    * */

    public void sendMessage(String msg, String photoUrl, String friendEmail, User myUser,
                            final SendMessageListener listener){
        Message message = new Message();
        message.setSender(myUser.getEmail());
        message.setMsg(msg);
        message.setPhotoUrl(photoUrl);

        DatabaseReference chatReference = getChatsMessagesReference(myUser.getEmail(), friendEmail);
        chatReference.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                listener.onSuccess();
            }
        });
    }
}
