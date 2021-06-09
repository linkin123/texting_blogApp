package com.example.texting.loginModule.model.dataAccess;

import androidx.annotation.NonNull;

import com.example.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import com.example.texting.common.model.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public void onResume() {
        mAuthenticationAPI.getFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }

    public void onPause() {
        if (mAuthenticationAPI != null) {
            mAuthenticationAPI.getFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        }
    }

    public void getStatusAuth(StatusAuthCallback callback) {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    callback.onGetUser(user);
                } else {
                    callback.onLaunchUILogin();
                }
            }
        };
    }

    public User getCurrentUser() {
        return mAuthenticationAPI.getAuthUser();
    }

}
