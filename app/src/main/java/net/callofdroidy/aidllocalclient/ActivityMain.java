package net.callofdroidy.aidllocalclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.callofdroidy.aidlremoteservice.RemoteWebpage;

public class ActivityMain extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG="ClientActivity";
    TextView tv_show ;
    Button btn_bind ;
    Button btn_getInfo;
    String actionName = "net.callofdroidy.aidlquerywebpage";
    RemoteWebpage remoteWebPage = null;
    String allInfo = null;
    boolean isBinded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_show = (TextView) findViewById(R.id.tv_show);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_getInfo = (Button)findViewById(R.id.btn_get);

    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.e(TAG, "onPause");
        if(isBinded){
            Log.e(TAG, "unbind");
            unbindService(connection);
        }
    }

    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "connecting");
            remoteWebPage = RemoteWebpage.Stub.asInterface(service);
            if (remoteWebPage == null) {
                tv_show.setText("bind service failed!");
                return;
            }
            try {
                isBinded = true;
                btn_bind.setText("disconnected");
                tv_show.setText("connected!");
                allInfo = remoteWebPage.getCurrentPageUrl();
                btn_getInfo.setEnabled(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected...");
        }
    }

    MyServiceConnection connection = new MyServiceConnection();

    @Override
    public void onClick(View v) {
        if(v==this.btn_bind){
            if(!isBinded){
                Log.e("arrive here", "");
                Intent intent  = new Intent(actionName);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }else{
                Log.e(TAG, "disconnecting.");
                unbindService(connection);
                btn_getInfo.setEnabled(false);
                btn_bind.setText("connect");
                isBinded = false;
                tv_show.setText("disconnected!");
            }
        }else if(v==this.btn_getInfo){
            tv_show.setText(allInfo);
        }

    }

}
