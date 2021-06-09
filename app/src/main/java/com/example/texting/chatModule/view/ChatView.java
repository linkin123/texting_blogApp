package com.example.texting.chatModule.view;

import android.content.Intent;

import com.example.texting.common.model.pojo.Message;

public interface ChatView {

    void showProgress();
    void hideProgress();

    void onStatusUsser(boolean connected , long lastConnection);

    void onError(int resMsg);

    void onMessageReceived(Message msg);

    void openDialogPreview(Intent data);
}
