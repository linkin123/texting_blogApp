package com.example.texting.profileModule.view;

import android.content.Intent;

public interface ProfileView {
    void enabledUIElements();
    void disabledUIElements();

    void showProgress();
    void hideProgress();

    void showProgressImage();
    void hideProgressImage();

    /*muestra datos del usuario logeado*/
    void showUserData(String username, String email, String photoUrl);
    /*lanza la galeria*/
    void launchGallery();
    /*abre dialogo para seleccionar de galeria*/
    void openDialogPreview(Intent data);
    /*comportamiento edicion*/
    void menuEditMode();
    void menuNormalMode();

    void saveUsernameSuccess();
    void updateImageSuccess(String photoUrl);
    void setResultOK(String username, String photoUrl);

    void onErrorUpload(int resMgs);
    void onError(int resMgs);

}
