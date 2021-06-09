package com.example.texting.loginModule;

import android.app.Activity;
import android.content.Intent;
import android.util.EventLog;

import com.example.texting.R;
import com.example.texting.loginModule.events.LoginEvent;
import com.example.texting.loginModule.model.LoginInteractor;
import com.example.texting.loginModule.model.LoginInteractorClass;
import com.example.texting.loginModule.view.LoginActivity;
import com.example.texting.loginModule.view.LoginView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class LoginPresenterClass implements LoginPresenter {
    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenterClass(LoginView mView) {
        this.mView = mView;
        this.mInteractor = new LoginInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        if (setProgress()) {
            mInteractor.onResume();
        }
    }

    @Override
    public void onPause() {
        if(setProgress()){
            mInteractor.onPause();
        }
    }

    private boolean setProgress() {
        if(mView != null){
            mView.showProgress();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mView = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case LoginActivity.RC_SIGN_IN:
                    if(data != null){
                        mView.showLoginSuccessfully(data);
                    }
                    break;
            }
        }else{
            mView.showError(R.string.login_message_error);
        }
    }

    @Override
    public void getStatusAuth() {
        if(setProgress()){
            mInteractor.getStatusAuth();
        }
    }

    @Subscribe
    @Override
    public void onEventListener(LoginEvent event) {
        if(mView != null){
            mView.hideProgress();
            switch (event.getTypeEvent()){
                case LoginEvent.STATUS_AUTH_SUCCESS:
                    if(setProgress()){
                        mView.showMessageStarting();
                        mView.openMainActivity();
                    }
                    break;
                case LoginEvent.STATUS_AUTH_ERROR:
                    mView.openUILogin();
                    break;
                case LoginEvent.ERROR_SERVER:
                    mView.showError(event.getResMsg());
                    break;
            }
        }
    }
}
