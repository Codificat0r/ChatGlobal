package com.example.carlos.globalchat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by carlos on 16/01/18.
 */

public class ClientThread extends Thread {

    private Socket cliente;
    private String message;
    private boolean error;
    private int puerto;
    private String ip;
    private ChatServerConversation callback;

    public ClientThread(int port, String ip, String message, ChatServerConversation callback) throws IOException {
        try {
            this.callback = callback;
            this.puerto = port;
            this.ip = ip;
            this.message = message;
            error = false;
        } catch (Exception e) {
            error = true;
        }
    }

    @Override
    public void run() {
        super.run();

        try {
            if (!error && !message.isEmpty()) {
                cliente = new Socket(ip, puerto);

                OutputStream os = cliente.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);

                bw.write(message);
                bw.close();

                callback.actualizarChat(message, ChatActivity.ENVIADO);

                cliente.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
