package com.example.texting.profileModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.texting.profileModule.events.ProfileEvent;

public interface ProfilePresenter {
    void onCreate();
    void onDestroy();

    void setupUser(String username, String email, String photoUrl);
    void checkMode();

    void updateUsername(String username);
    void updateImage(Activity activity , Uri uri);

    void result(int requestCode, int resultCode, Intent data);

    void onEventListener(ProfileEvent event);
}
