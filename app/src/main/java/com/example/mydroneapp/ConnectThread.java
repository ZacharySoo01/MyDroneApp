package com.example.mydroneapp;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.view.View;
import android.widget.TextView;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("ConnectThread",  "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery();


        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            Log.e("ConnectThread", "Unable to connect; close the socket and return", connectException);
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("ConnectThread", "Could not close the client socket", closeException);
            }
            return;
        }
        Log.i("ConnectThread", "Connected Successfully");
        manageMyConnectedSocket(mmSocket);

    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        // Perform work associated with the connected socket in this method

        try {
            // Example: Get InputStream and OutputStream from the BluetoothSocket
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // Example: Reading data from the InputStream
            byte[] buffer = new byte[1024];
            int bytesRead;

            while (true) {
                bytesRead = inputStream.read(buffer);

                // Process the read data as needed

                // Example: Writing data to the OutputStream
                String message = "Hello, Raspberry Pi!";
                byte[] messageBytes = message.getBytes();
                outputStream.write(messageBytes);
            }
        } catch (IOException e) {
            // Handle exceptions (e.g., connection lost)
            e.printStackTrace();
        } finally {
            // Clean up resources if needed
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
            Log.i("ConnectThread", "Closed Successfully");
        } catch (IOException e) {
            Log.e("ConnectThread", "Could not close the client socket", e);
        }
    }
}
