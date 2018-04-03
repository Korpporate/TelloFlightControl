package com.rakapps.telloflightcontrol;

import android.os.AsyncTask;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDP_Server {

    private boolean serverActive = true;
    private String droneResponse;

//    @SuppressLint("NewApi")
    public void runUdpServer()
    {
        new AsyncTask<Void, Void, Void>()
        {
           @Override
            protected Void doInBackground(Void... params)
            {
                byte[] lMsg = new byte[MainActivity.DRONE_BUFFER_SIZE];
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);

                try
                {
                    if(MainActivity.UPD_SOCKET == null) {
                        MainActivity.UPD_SOCKET = new DatagramSocket(MainActivity.LOCAL_PORT);
                    }

                    while(serverActive)
                    {
                        MainActivity.UPD_SOCKET.receive(dp);
                        droneResponse = new String(lMsg, 0, dp.getLength());
                        publishProgress();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    MainActivity.EXCEPTION_ERROR_SERVER = true;
                }
                finally
                {
                    if (MainActivity.UPD_SOCKET != null)
                    {
                        MainActivity.UPD_SOCKET.close();
                    }
                }

                return null;
            }

            protected void onProgressUpdate(Void... progress) {
                MainActivity.DRONE_SOCKET_ACTIVE = true;
                MainActivity.TEXT_RESPONSE.setText(droneResponse.trim());
            }

            protected void onPostExecute(Void result) {
                MainActivity.DRONE_SOCKET_ACTIVE = false;
                MainActivity.TEXT_RESPONSE.setText("Error. UDP server loop ended unexpectedly!");
                super.onPostExecute(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stop_UDP_Server()
    {
        serverActive = false;
    }
}