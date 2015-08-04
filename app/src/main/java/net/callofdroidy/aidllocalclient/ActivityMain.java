package net.callofdroidy.aidllocalclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.callofdroidy.aidldemo.aidl.RemoteWebPage;

import java.util.List;

public class ActivityMain extends AppCompatActivity{

    private final static String TAG = "ClientActivity";
    TextView tv_show ;
    Button btn_bind ;
    Button btn_unbind;
    Button btn_getInfo;
    String actionName = "net.callofdroidy.aidlquerywebpage";
    RemoteWebPage remoteWebPage = null;
    String allInfo = null;
    boolean isBound = false;
    MyServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_show = (TextView) findViewById(R.id.tv_show);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_unbind = (Button) findViewById(R.id.btn_unbind);
        btn_getInfo = (Button)findViewById(R.id.btn_get);

        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBound){
                    //Since android 5.0, Service Intent must be explicit
                    //for details, http://developer.android.com/guide/components/intents-filters.html#ExampleExplicit

                    /*
                    Intent intentImplicit  = new Intent(RemoteWebPage.class.getName());
                    List<ResolveInfo> matches = getPackageManager().queryIntentServices(intentImplicit, 0);

                    if (matches.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Cannot find a matching service!",
                                Toast.LENGTH_LONG).show();
                    }
                    else if (matches.size() > 1) {
                        Toast.makeText(getApplicationContext(), "Found multiple matching services!",
                                Toast.LENGTH_LONG).show();
                    }else {
                        Intent intentExplicit = new Intent(intentImplicit);
                        ServiceInfo serviceInfo = matches.get(0).serviceInfo;
                        ComponentName componentName = new ComponentName(serviceInfo.applicationInfo.packageName, serviceInfo.name);
                        intentExplicit.setComponent(componentName);
                        bindService(intentExplicit, connection, Context.BIND_AUTO_CREATE);
                        Log.e(TAG, "connecting");
                    }
                    */

                    connection =  new MyServiceConnection();
                    Intent i = new Intent(ActivityMain.this, RemoteWebPage.class);
                    //i.setClassName(ActivityMain.this, RemoteWebPage.class.getName());
                    bindService(i, connection, Context.BIND_AUTO_CREATE);
                }else{
                    Log.e(TAG, "bound already");
                }
            }
        });

        btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound){
                    unbindService(connection);
                }
            }
        });

        btn_getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound){
                    tv_show.setText(allInfo);
                }

            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if(isBound){
            Log.e(TAG, "unbind");
            unbindService(connection);
        }
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "connected");
            remoteWebPage = RemoteWebPage.Stub.asInterface(service);
            if (remoteWebPage == null) {
                tv_show.setText("bind service failed!");
                return;
            }
            try {
                isBound = true;
                tv_show.setText("connected!");
                allInfo = remoteWebPage.getCurrentPageUrl();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected...");
            isBound = false;
            tv_show.setText("disconnected!");
        }
    }


}
