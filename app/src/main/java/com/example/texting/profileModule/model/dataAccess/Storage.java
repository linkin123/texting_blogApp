package com.example.texting.profileModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.texting.R;
import com.example.texting.common.StorageUploadImageCallback;
import com.example.texting.common.model.dataAccess.FirebaseStorageAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Storage {
    private static final String PATH_PROFILE = "profile";
    private FirebaseStorageAPI mStorageAPI;

    public Storage() {
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    public void uploadImageProfile(Activity activity, Uri imageUri, String email,
                                   StorageUploadImageCallback callback){
        if(imageUri.getLastPathSegment() != null){
            final StorageReference photoRef = mStorageAPI.getPhotosReferenceByEmail(email)
                    .child(PATH_PROFILE).child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(uri != null){
                                callback.onSuccess(uri);
                            }else{
                                callback.onError(R.string.profile_error_imageUpdated);
                            }
                        }
                    });
                }
            });

        }else{
            callback.onError(R.string.profile_error_invalid_image);
        }
    }

    public void deleteOldImage(String oldPhotoUrl, String downloadUrl){
        if(oldPhotoUrl != null && !oldPhotoUrl.isEmpty()){
            StorageReference storageReference = mStorageAPI.getmFirebaseStorage()
                    .getReferenceFromUrl(downloadUrl);

            StorageReference oldStorageReference = null;

            try {
                oldStorageReference = mStorageAPI.getmFirebaseStorage()
                        .getReferenceFromUrl(oldPhotoUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*si la url no es la misma procedemos a eliminar*/
            if(oldStorageReference != null &&
                    !oldStorageReference.getPath().equals(storageReference.getPath())){
                oldStorageReference.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failure");
                    }
                });
            }
        }
    }
}
