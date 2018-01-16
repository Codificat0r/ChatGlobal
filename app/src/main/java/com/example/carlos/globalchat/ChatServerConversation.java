package com.example.carlos.globalchat;

/**
 * Created by carlos on 16/01/18.
 */

public interface ChatServerConversation {
    void actualizarChat(String message, int sender);
    void actualizarMiIp(String ip);
}
