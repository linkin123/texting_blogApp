package com.example.texting.mainModule;

import com.example.texting.common.model.pojo.User;
import com.example.texting.mainModule.events.MainEvent;

public interface MainPresenter {

    void onCreate();
    void onDestroy();
    void onPause();
    void onResume();

    void signOff();
    User getCurrentUser();
    void removeFriend(String friendUid);

    void acceptRequest(User user);
    void denyRequest(User user);

    void onEventListener(MainEvent event);
}
