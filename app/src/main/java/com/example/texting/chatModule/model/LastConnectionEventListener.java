package com.example.texting.chatModule.model;

public interface LastConnectionEventListener {
    void onSuccess(boolean online, long lastConnection , String uidConnectedFriend);

}
