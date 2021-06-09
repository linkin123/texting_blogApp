package com.example.texting.mainModule.model;

import com.example.texting.common.model.pojo.User;

public interface MainInteractor {
    void subscribeToUserList();
    void unSubscribeTpUserList();

    void singOff();

    User getCurrentUser();
    void removeFriend(String friendUid);

    void acceptRequest(User user);
    void denyRequest(User user);
}
