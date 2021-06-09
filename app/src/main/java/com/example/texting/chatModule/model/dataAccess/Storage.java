package com.example.texting.chatModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;

import com.example.texting.R;
import com.example.texting.common.StorageUploadImageCallback;
import com.example.texting.common.model.dataAccess.FirebaseStorageAPI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Storage {
    private static final String PATH_CHATS = "chats";

    private FirebaseStorageAPI mStorageAPI;

    public Storage() {
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    /*
    * Image chat
    * */

    public void uploadImageChat(Activity activity, final Uri imageUri, String myEmail,
                                final StorageUploadImageCallback callback){
        if(imageUri.getLastPathSegment() != null){
            StorageReference photoref = mStorageAPI.getPhotosReferenceByEmail(myEmail).child(PATH_CHATS)
                    .child(imageUri.getLastPathSegment());

            photoref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(uri != null){
                                callback.onSuccess(uri);
                            }else{
                                callback.onError(R.string.chat_error_imageUpload);
                            }
                        }
                    });
                }
            });
        }
    }
}
