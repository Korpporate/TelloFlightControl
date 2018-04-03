package com.rakapps.telloflightcontrol;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDP_Client {

    public String Message;

    @SuppressLint("NewApi")
    public void sendMessage() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(MainActivity.UPD_SOCKET == null) {
                        MainActivity.UPD_SOCKET = new DatagramSocket(MainActivity.LOCAL_PORT);
                    }
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(),
                                            Message.length(),
                                            MainActivity.DRONE_ADDRESS,
                                            MainActivity.DRONE_PORT);

                    MainActivity.UPD_SOCKET.send(dp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    MainActivity.DRONE_SOCKET_ACTIVE = false;
                    MainActivity.EXCEPTION_ERROR_CLIENT = true;
                    }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}