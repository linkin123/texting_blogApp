package com.example.texting.common;

import android.net.Uri;

public interface StorageUploadImageCallback {
    void onSuccess(Uri newUri);
    void onError(int resMsg);
}