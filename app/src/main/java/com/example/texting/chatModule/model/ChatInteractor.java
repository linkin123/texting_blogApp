package com.example.texting.chatModule.model;

import android.app.Activity;
import android.net.Uri;

public interface ChatInteractor {
    void subscribeToFriend(String friendUid, String friendEmail);
    void unsubscribeToFriend(String friendUid);

    void subscribeToMessage();
    void unsubscribeToMessage();

    void sendMessage(String msg);
    void sendImage(Activity activity, Uri imageUri);

}
