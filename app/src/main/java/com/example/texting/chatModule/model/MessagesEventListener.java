package com.example.texting.chatModule.model;

import com.example.texting.common.model.pojo.Message;

public interface MessagesEventListener {
    void onMessageReceived(Message message);
    void onError(int resMsg);

}
