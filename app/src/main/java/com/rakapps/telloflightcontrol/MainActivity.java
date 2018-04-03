package com.rakapps.telloflightcontrol;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private boolean exceptionErrorInetAddress = false;
    public static boolean EXCEPTION_ERROR_CLIENT = false;
    public static boolean EXCEPTION_ERROR_SERVER = false;
    public static InetAddress DRONE_ADDRESS;
    public static final int LOCAL_PORT = 9000;
    public static final int DRONE_BUFFER_SIZE = 1518;
    public static final int DRONE_PORT = 8889;
    public static DatagramSocket UPD_SOCKET = null;
    private AutoCompleteTextView textCommand;
    public static TextView TEXT_RESPONSE;
    private String[] mCmdArray;
    private UDP_Server udpServer;
    private UDP_Client udpClient;
    public static boolean DRONE_SOCKET_ACTIVE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Snackbar snackBar = Snackbar.make(view, "command takeoff land cw ccw forward back left right up down flip speed speed? battery?", Snackbar.LENGTH_INDEFINITE);
                snackBar.show();

                snackBar.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                });
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);


        try {
            DRONE_ADDRESS = InetAddress.getByName("192.168.10.1");
            // UnknownHostException
        } catch (Exception e) {
            exceptionErrorInetAddress = true;
        }

        if(!exceptionErrorInetAddress) {
            udpServer = new UDP_Server();
            udpServer.runUdpServer();
            udpClient = new UDP_Client();
        }

        textCommand = (AutoCompleteTextView) findViewById(R.id.textCmd);
        TEXT_RESPONSE = (TextView) findViewById(R.id.textResponse);

        //Creating the instance of ArrayAdapter containing list of drone commands.
        mCmdArray = getResources().getStringArray(R.array.Commands);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, mCmdArray);

        //Getting the instance of AutoCompleteTextView
//        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.textCmd);
        textCommand.setThreshold(1);//will start working from first character
        textCommand.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

    @Override
    protected void onResume() {
        if(exceptionErrorInetAddress) {
            TEXT_RESPONSE.setText("Exception error creating InetAddress!");
            Button button = (Button) findViewById(R.id.btnSendCmd);
            button.setEnabled(false);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        // If the app loses focus, or phone is locked, then land!
        udpClient.Message = "land";
        udpClient.sendMessage();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        udpServer.stop_UDP_Server();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Toast.makeText(this, "App version: " + versionName + "\nBlue Spectrum Software", Toast.LENGTH_LONG).show();
            } catch(PackageManager.NameNotFoundException e) {}
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSendCmd(View v)
    {
        if(EXCEPTION_ERROR_CLIENT) {
            TEXT_RESPONSE.setText("Exception error in UDP client.");
        } else if(EXCEPTION_ERROR_SERVER) {
            TEXT_RESPONSE.setText("Exception error in UDP server.");
        } else {
            String cmd = textCommand.getText().toString().trim();

            textCommand.setText("");
//            textCommand.clearFocus();

            if(DRONE_SOCKET_ACTIVE) {
                TEXT_RESPONSE.setText("");
            }

            // If user presses button with no command entered, land immediately!
            if (cmd.isEmpty()) {
                cmd = "land";
            }

            udpClient.Message = cmd;
            udpClient.sendMessage();
            Toast.makeText(this, "Sent: (" + cmd + ")", Toast.LENGTH_SHORT).show();
        }
    }
}
