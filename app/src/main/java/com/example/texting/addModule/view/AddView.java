package com.example.texting.addModule.view;

public interface AddView {
    void enabledUIElements();
    void disabledUIElements();
    void showProgress();
    void hideProgress();

    void friendAdded();
    void friendNotAdded();
}
