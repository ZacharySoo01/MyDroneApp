package com.example.mydroneapp;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;
import java.io.IOException;
import java.util.UUID;


public class Server{	
	public static void main (String []args){
		AcceptThread a = new AcceptThread();
		a.run();
	}
}

class AcceptThread extends Thread {
   private final BluetoothServerSocket mmServerSocket;
   private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

   public AcceptThread() {
       // Use a temporary object that is later assigned to mmServerSocket
       // because mmServerSocket is final.
       BluetoothServerSocket tmp = null;
       try {
           // MY_UUID is the app's UUID string, also used by the client code.
           UUID MY_UUID = UUID.fromString("00001818-0000-1000-8000-00805f9b34fb");
           String NAME = "My Bluetooth connection";
           tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
       } catch (IOException e) {
           System.out.println("Socket's listen() method failed");
       }
       mmServerSocket = tmp;
   }

   public void run() {
       BluetoothSocket socket = null;
       // Keep listening until exception occurs or a socket is returned.
       while (true) {
           try {
               socket = mmServerSocket.accept();
           } catch (IOException e) {
               System.out.println("Socket's accept() method failed");
               break;
           }

           try {
               if (socket != null) {
                   mmServerSocket.close();
                   break;
               }
           } catch (IOException e){
               return;
           }
       }
   }

   // Closes the connect socket and causes the thread to finish.
   public void cancel() {
       try {
           mmServerSocket.close();
       } catch (IOException e) {
           System.out.println("Could not close the connect socket");
       }
   }
}


