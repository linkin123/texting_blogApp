package com.example.texting.chatModule.model;

import android.app.Activity;
import android.net.Uri;

import com.example.texting.chatModule.events.ChatEvent;
import com.example.texting.chatModule.model.dataAccess.NotificationRS;
import com.example.texting.chatModule.model.dataAccess.RealtimeDatabase;
import com.example.texting.chatModule.model.dataAccess.Storage;
import com.example.texting.common.Constants;
import com.example.texting.common.StorageUploadImageCallback;
import com.example.texting.common.model.EventErrorTypeListener;
import com.example.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.example.texting.common.model.pojo.Message;
import com.example.texting.common.model.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class ChatInteractorClass implements ChatInteractor {

    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private Storage mStorage;
    //notify
    private NotificationRS mNotification;

    private User mMyUser;
    private String mFriendUid;
    private String mFriendEmail;

    private long mLastConnectionFriend;
    private String mUidConnectedFriend = "";

    public ChatInteractorClass() {
        this.mDatabase = new RealtimeDatabase();
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
        this.mStorage = new Storage();
        this.mNotification = new NotificationRS();
    }

    private User getCurrentUser(){
        if(mMyUser == null){
            mMyUser = mAuthenticationAPI.getAuthUser();
        }
        return mMyUser;
    }

    @Override

    public void subscribeToFriend(String friendUid, String friendEmail) {
        this.mFriendEmail = friendEmail;
        this.mFriendUid = friendUid;

        mDatabase.subscribeToFriend(friendUid, new LastConnectionEventListener() {
            @Override
            public void onSuccess(boolean online, long lastConnection, String uidConnectedFriend) {
                postStatusFriend(online, lastConnection);
                mUidConnectedFriend = uidConnectedFriend;
                mLastConnectionFriend = lastConnection;
            }
        });

        mDatabase.setMessageRead(getCurrentUser().getUid(), friendUid);
    }


    @Override
    public void unsubscribeToFriend(String friendUid) {
        mDatabase.unsubscribeToFriend(friendUid);
    }

    @Override
    public void subscribeToMessage() {
        mDatabase.subscribeToMessages(getCurrentUser().getEmail() , mFriendEmail,
                new MessagesEventListener() {
                    @Override
                    public void onMessageReceived(Message message) {
                        String msgSender = message.getSender();
                        message.setSentByMe(msgSender.equals(getCurrentUser().getEmail()));
                        postMessage(message);


                    }


                    @Override
                    public void onError(int resMsg) {
                        post(ChatEvent.ERROR_SERVER, resMsg);
                        /*ideal para bloquear pantalla al otro usuario si alguien lo elimina*/
                    }
                });

        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.ONLINE, mFriendUid, getCurrentUser().getUid());
    }

    @Override
    public void unsubscribeToMessage() {
        mDatabase.unsubscribeToMessage(getCurrentUser().getEmail() , mFriendEmail);
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.OFFLINE, getCurrentUser().getUid());
    }

    @Override
    public void sendMessage(String msg) {
        sendMessage(msg , null);
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        mStorage.uploadImageChat(activity, imageUri, getCurrentUser().getEmail(),
                new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        sendMessage(null, newUri.toString());
                        postUploadSuccess();
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ChatEvent.IMAGE_UPLOAD_FAIL , resMsg);
                    }
                });
    }

    private void postUploadSuccess() {
        post(ChatEvent.IMAGE_UPLOAD_SUCCESS, 0, null , false, 0);
    }

    private void sendMessage(final String msg, String photoUrl){
        mDatabase.sendMessage(msg, photoUrl, mFriendEmail, getCurrentUser(), new SendMessageListener() {
            @Override
            public void onSuccess() {
                if(!mUidConnectedFriend.equals(getCurrentUser().getUid())){
                    mDatabase.sumUnreadMessages(getCurrentUser().getUid() , mFriendUid);
                    mNotification.sendNotification(getCurrentUser().getUsername() , msg,
                            mFriendEmail, getCurrentUser().getUid() , getCurrentUser().getEmail(),
                            getCurrentUser().getUri() , new EventErrorTypeListener() {
                                @Override
                                public void onError(int typeEvent, int resMsg) {
                                    post(typeEvent , resMsg);
                                }
                            });
                }
            }
        });
    }

    private void postMessage(Message message){
        post(ChatEvent.MESSAGE_ADDED , 0, message, false, 0);
    }

    private void post(int typeEvent , int resMsg){
        post(typeEvent, resMsg, null, false, 0);
    }

    private void postStatusFriend(boolean online, long lastConnection) {
        post(ChatEvent.GET_STATUS_FRIEND, 0, null, online, lastConnection);
    }

    private void post(int typeEvent, int resMsg, Message message, boolean online, long lastConnection){
        ChatEvent event = new ChatEvent();
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        event.setMessage(message);
        event.setConnected(online);
        event.setLastConnection(lastConnection);
        EventBus.getDefault().post(event);

    }


}
