package com.example.texting.mainModule.model.dataAccess;

import com.example.texting.common.model.pojo.User;

public interface UserEventListener {
    void onUserAdded(User user);
    void onUserUpdated(User user);
    void onUserRemoved(User user);

    void onError(int resMsg);
}
