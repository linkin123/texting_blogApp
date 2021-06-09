package com.example.texting.addModule.model;

import com.example.texting.addModule.events.AddEvent;
import com.example.texting.addModule.model.dataAccess.RealtimeDatabase;
import com.example.texting.common.model.BaseEventCallback;
import com.example.texting.common.model.dataAccess.FirebaseAuthenticationAPI;

import org.greenrobot.eventbus.EventBus;

public class AddInteractorClass implements AddInteractor{

    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public AddInteractorClass() {
        this.mDatabase = new RealtimeDatabase();
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    @Override
    public void addFriend(String email) {
        mDatabase.addFriend(email, mAuthenticationAPI.getAuthUser(), new BaseEventCallback() {
            @Override
            public void onSuccess() {
                post(AddEvent.SEND_REQUEST_SUCCESS);
            }

            @Override
            public void onError() {
                post(AddEvent.ERROR_SERVER);

            }
        });
    }

    private void post(int typeEvent) {
        AddEvent event = new AddEvent();
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }



}
