package com.sekhgmainuddin.calculator.whatsappclone.interfaces;

import com.sekhgmainuddin.calculator.whatsappclone.models.chat.Chats;

import java.util.List;

public interface OnReadChatCallBack {

    void OnReadSuccess(List<Chats> list);
    void OnReadFailed();

}
