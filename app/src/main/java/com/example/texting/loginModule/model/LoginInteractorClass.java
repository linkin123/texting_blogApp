package com.example.texting.loginModule.model;

import com.example.texting.common.model.EventErrorTypeListener;
import com.example.texting.common.model.dataAccess.FirebaseCloudMessagingAPI;
import com.example.texting.common.model.pojo.User;
import com.example.texting.loginModule.events.LoginEvent;
import com.example.texting.loginModule.model.dataAccess.Authentication;
import com.example.texting.loginModule.model.dataAccess.RealtimeDatabase;
import com.example.texting.loginModule.model.dataAccess.StatusAuthCallback;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

public class LoginInteractorClass implements LoginInteractor {

    private Authentication mAuthentication;
    private RealtimeDatabase mDatabase;
    //notify
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;


    public LoginInteractorClass() {
        mAuthentication = new Authentication();
        mDatabase = new RealtimeDatabase();
        //notify
        mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void onResume() {
        mAuthentication.onResume();
    }

    @Override
    public void onPause() {
        mAuthentication.onPause();
    }

    @Override
    public void getStatusAuth() {
        mAuthentication.getStatusAuth(new StatusAuthCallback() {
            @Override
            public void onGetUser(FirebaseUser user) {
                post(LoginEvent.STATUS_AUTH_SUCCESS , user);

                mDatabase.checkUserExist(mAuthentication.getCurrentUser().getUid(), new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        if(typeEvent == LoginEvent.USER_NOT_EXIST){
                            registerUser();
                        }else{
                            post(typeEvent);
                        }
                    }
                });

                mCloudMessagingAPI.subscribeToMyTopic(user.getEmail());
            }

            @Override
            public void onLaunchUILogin() {
                post(LoginEvent.STATUS_AUTH_ERROR);
            }
        });
    }

    private void registerUser() {
        User currentUser = mAuthentication.getCurrentUser();
        mDatabase.registerUser(currentUser);
    }

    private void post(int typeEvent) {
        post(typeEvent, null);
/*        post(LoginEvent.STATUS_AUTH_ERROR);*/
    }

    private void post(int typeEvent, FirebaseUser user) {
        LoginEvent event = new LoginEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        EventBus.getDefault().post(event);

    }
}
