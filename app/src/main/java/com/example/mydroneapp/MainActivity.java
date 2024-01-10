package com.example.mydroneapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.os.AsyncTask;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.InputStream;

// Backend for the drone app
public class MainActivity extends AppCompatActivity {
    private TextView textViewStatus;
    private Button connect;
    private Button disconnect;
    private Button testcomm;

    private static Session session;
    private static JSch jsch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStatus = findViewById(R.id.textView2);
        connect = findViewById(R.id.buttonConnect);
        disconnect = findViewById(R.id.buttonDisconnect);
        testcomm = findViewById(R.id.buttonTest);

        // Connect button
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // connect to the pi thru ssh
                new SSHconnect().execute();
                textViewStatus.setText("Connection Status: Connected");
            }
        });
        // Disconnect button
        disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // disconnect to the pi thru ssh
               new SSHdisconnect().execute();
               textViewStatus.setText("Connection Status: Disconnected");
            }
        });
        // Test communication button
        testcomm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SSHsendCommand().execute();
            }
        });

    }

    private static class SSHconnect extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            try {
                jsch = new JSch();
                // just change host ip, username, password
                session = jsch.getSession("pi", "192.168.10.180", 22);
                session.setPassword("5313");
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
            } catch (JSchException e) {
                Log.e("connect", "Error: " + e.getMessage());
            }
            return "";
        }
        protected void onPostExecute(String result) {
            // Handle the SSH result here
            Log.d("connect", "successful");
        }
    }

    private static class SSHdisconnect extends AsyncTask<Void, Void, String>{
        protected String doInBackground(Void... voids){
            session.disconnect();
            return "";
        }
        protected void onPostExecute(String result) {
            // Handle the SSH result here
            Log.d("disc", "successful");
        }
    }

    public static class SSHsendCommand extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            try{
                // Example command execution
                String command = "ls";
                ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                channelExec.setCommand(command);

                // Get command output
                java.io.InputStream in = channelExec.getInputStream();
                channelExec.connect();

                byte[] tmp = new byte[1024];
                StringBuilder output = new StringBuilder();

                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        output.append(new String(tmp, 0, i));
                    }
                    if (channelExec.isClosed()) {
                        if (in.available() > 0) continue;
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {}
                }
                channelExec.disconnect();
                return output.toString();
            } catch (JSchException | IOException e){
                Log.e("comm", "Error: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }
        protected void onPostExecute(String result) {
            // Handle the SSH result here
            Log.d("communicate", "SSH Result: " + result);
        }
    }

}