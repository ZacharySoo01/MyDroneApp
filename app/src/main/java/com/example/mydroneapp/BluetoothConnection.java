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
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnection extends AsyncTask<Void, Void, Void> {

    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean connected = false;

    protected Void doInBackground(Void... params) {
        // Replace "00:00:00:00:00:00" with the Bluetooth MAC address of your Raspberry Pi
        String raspberryPiAddress = "DC:A6:32:B9:93:44";

        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(raspberryPiAddress);
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // RFCOMM UUID


            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            connected = true;

            // Send data to Raspberry Pi
            OutputStream outputStream = socket.getOutputStream();
            String message = "Hello from Android!";
            outputStream.write(message.getBytes());

        } catch (IOException e) {
            Log.e("BluetoothConnection", "Error during Bluetooth connection", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (connected) {
            Log.d("BluetoothConnection", "Connected successfully");
        } else {
            Log.e("BluetoothConnection", "Failed to connect");
        }
    }
}

