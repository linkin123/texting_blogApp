package com.example.texting.mainModule.model.dataAccess;

import com.example.texting.common.model.dataAccess.FirebaseAuthenticationAPI;

public class Authentication {

    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication(){
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAuthenticationAPI;
    }

    public void singOff(){
        mAuthenticationAPI.getFirebaseAuth().signOut();
    }
    
}
