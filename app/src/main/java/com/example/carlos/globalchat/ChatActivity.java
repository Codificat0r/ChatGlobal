package com.example.carlos.globalchat;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ChatActivity extends AppCompatActivity implements ChatServerConversation {

    public static final int ENVIADO = 0;
    public static final int RECEPTOR = 1;

    private LinearLayout lnlyMensajes;
    private FloatingActionButton fab;
    private EditText edtMensaje;
    private int maxWidthMessage = 220;
    private ArrayList<String> ips;
    private String miIp;

    private final static int PUERTO = 4444;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ips = new ArrayList<>();

        lnlyMensajes = findViewById(R.id.lnlyMensajes);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerIpsServers();
                abrirSockets();
            }
        });

        edtMensaje = findViewById(R.id.edtMensaje);

        new ServerThread(PUERTO, this).start();

        try {
            new ClientThread(PUERTO, "80.102.112.41", "hola", this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //actualizarChat("hola", ENVIADO);
        //actualizarChat("hola", RECEPTOR);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    @Override
    public void actualizarChat(final String message, final int sender) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView txv = new TextView(ChatActivity.this);
                txv.setTextColor(Color.WHITE);

                if (sender == RECEPTOR) {
                    txv.setGravity(Gravity.LEFT);
                } else {
                    txv.setGravity(Gravity.RIGHT);
                }
                txv.setMaxWidth(maxWidthMessage);
                txv.setText(message);


                lnlyMensajes.addView(txv);
            }
        });

    }

    @Override
    public void actualizarMiIp(String ip) {
        miIp = ip;
    }

    private void obtenerIpsServers() {
        String urlIps = "http://alumno.mobi/~alumno/superior/cruz/ips.txt";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlIps);
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String linea;

            ips.clear();

            while ((linea = rd.readLine()) != null) {

                if (!ips.contains(linea)) {
                    ips.add(linea);
                }
            }

            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirSockets() {
        for (String tmp:ips) {
            try {
                new ClientThread(PUERTO, tmp, edtMensaje.getText().toString(), this).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
