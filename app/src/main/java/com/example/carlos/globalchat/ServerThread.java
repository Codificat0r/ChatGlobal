package com.example.carlos.globalchat;

import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by carlos on 16/01/18.
 */

public class ServerThread extends Thread {

    ServerSocket server;
    ChatServerConversation callback;
    int puerto;

    private final static String URLIP = "http://alumno.mobi/~alumno/superior/cruz/receptorIps.php";

    public ServerThread(int port, ChatServerConversation activity){
        this.puerto = port;
        callback = activity;
    }

    /*private String obtenerIp() {
        final String[] ip = new String[1];
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get("http://checkip.amazonaws.com", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ip[0] = responseString;
            }
        });

        return ip[0];
    }*/

    private String obtenerIp() {;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://checkip.amazonaws.com");
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String linea = rd.readLine();
            rd.close();
            return linea;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void informarIp() {

        String ip = obtenerIp();
        callback.actualizarMiIp(ip);

        /*AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.setTimeout(5000);
        cliente.setMaxRetriesAndTimeout(2, 3000);
        RequestParams params = new RequestParams();
        params.put("ip", ip);

        cliente.post(URLIP, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });*/


        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URLIP);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("ip", ip));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            client.execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();

        informarIp();

        try {
            server = new ServerSocket(puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                String linea;
                Socket entrada = server.accept();

                InputStream is = entrada.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                linea = br.readLine();

                if (!linea.isEmpty()) {
                    callback.actualizarChat(linea, ChatActivity.RECEPTOR);
                }
                br.close();
                entrada.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
