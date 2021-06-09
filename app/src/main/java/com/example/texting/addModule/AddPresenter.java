package com.example.texting.addModule;

import com.example.texting.addModule.events.AddEvent;

public interface AddPresenter {
    void onShow();
    void onDestroy();

    void addFriend(String email);
    void onEventListener(AddEvent event);
}
