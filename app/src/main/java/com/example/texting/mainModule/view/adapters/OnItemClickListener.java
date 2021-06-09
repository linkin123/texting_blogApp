package com.example.texting.mainModule.view.adapters;

import com.example.texting.common.model.pojo.User;

public interface OnItemClickListener {
    void onItemClick(User user);
    void onItemLongClick(User user);

    void onAcceptRequest(User user);
    void onDenyRequest(User user);
}
