package com.example.texting.mainModule.model;

import com.example.texting.common.Constants;
import com.example.texting.common.model.BaseEventCallback;
import com.example.texting.common.model.dataAccess.FirebaseCloudMessagingAPI;
import com.example.texting.common.model.pojo.User;
import com.example.texting.mainModule.events.MainEvent;
import com.example.texting.mainModule.model.dataAccess.Authentication;
import com.example.texting.mainModule.model.dataAccess.RealtimeDatabase;
import com.example.texting.mainModule.model.dataAccess.UserEventListener;

import org.greenrobot.eventbus.EventBus;

public class MainInteractorClass implements MainInteractor {

    private RealtimeDatabase mDatabase;
    private Authentication mAuthentication;
    //notify
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;

    private User myUser = null;

    public MainInteractorClass() {
        mDatabase = new RealtimeDatabase();
        mAuthentication = new Authentication();
        //notify
        mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void subscribeToUserList() {
        mDatabase.subscribeToUserList(getCurrentUser().getUid(), new UserEventListener() {
            @Override
            public void onUserAdded(User user) {
                post(MainEvent.USER_ADDED , user);
            }

            @Override
            public void onUserUpdated(User user) {
                post(MainEvent.USER_UPDATED , user);
            }

            @Override
            public void onUserRemoved(User user) {
                post(MainEvent.USER_REMOVED , user);
            }

            @Override
            public void onError(int resMsg) {
                postError(resMsg);
            }
        });

        mDatabase.subscribeToRequest(getCurrentUser().getEmail(), new UserEventListener() {
            @Override
            public void onUserAdded(User user) {
                post(MainEvent.REQUEST_ADDED , user);
            }

            @Override
            public void onUserUpdated(User user) {
                post(MainEvent.REQUEST_UPDATED , user);
            }

            @Override
            public void onUserRemoved(User user) {
                post(MainEvent.REQUEST_REMOVED , user);
            }

            @Override
            public void onError(int resMsg) {
                post(MainEvent.ERROR_SERVER);
            }
        });
        changeConnectionStatus(Constants.ONLINE);
    }

    private void changeConnectionStatus(boolean online) {
        mDatabase.getmDatabaseAPI().updateMyLastConnection(online , getCurrentUser().getUid());

    }


    @Override
    public void unSubscribeTpUserList() {
        mDatabase.unsubscribeToUser(getCurrentUser().getUid());
        mDatabase.unsubcribeToRequest(getCurrentUser().getEmail());

        changeConnectionStatus(Constants.OFFLINE);
    }

    @Override
    public void singOff() {
        //notify
        mCloudMessagingAPI.unsubscribeToMyTopic(getCurrentUser().getEmail());

        mAuthentication.singOff();
    }

    @Override
    public User getCurrentUser() {
        return myUser == null ? mAuthentication.getmAuthenticationAPI().getAuthUser() : myUser;
    }

    @Override
    public void removeFriend(String friendUid) {
        mDatabase.removeUser(friendUid, getCurrentUser().getUid(), new BaseEventCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.USER_REMOVED);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void acceptRequest(final User user) {
        mDatabase.acceptRequest(user, getCurrentUser(), new BaseEventCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.REQUEST_ACCEPTED, user);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void denyRequest(User user) {
        mDatabase.denyRequest(user, getCurrentUser().getEmail(), new BaseEventCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.REQUEST_DENIED);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    private void postError(int resMsg) {
        post(MainEvent.ERROR_SERVER , null, resMsg);
    }

    private void post(int typeEvent) {
        post(typeEvent, null , 0);
    }


    private void post( int typeEvent, User user) {
        post(typeEvent , user, 0);
    }


    private void post(int typeEvent, User user , int resMsg) {
        MainEvent event = new MainEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);

    }

}
