package com.example.texting.profileModule.model.dataAccess;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.texting.R;
import com.example.texting.common.StorageUploadImageCallback;
import com.example.texting.common.model.EventErrorTypeListener;
import com.example.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.example.texting.common.model.pojo.User;
import com.example.texting.profileModule.events.ProfileEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication() {
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAuthenticationAPI;
    }

    public void updateUsernameFirebaseProfile(User myUser, final EventErrorTypeListener listener) {
        FirebaseUser user = mAuthenticationAPI.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(myUser.getUsername())
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        listener.onError(ProfileEvent.ERROR_PROFILE, R.string.profile_error_userUpdated);
                    }
                }
            });
        }
    }

    public void updateImageFirebaseProfile(final Uri downloadUri, final StorageUploadImageCallback callback) {
        FirebaseUser user = mAuthenticationAPI.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess(downloadUri);
                    } else {
                        callback.onError(R.string.profile_error_imageUpdated);
                    }
                }
            });
        }

    }
}
