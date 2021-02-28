package com.network;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.network.internet.NetworkStateChangeListener;
import com.network.internet.NetworkStateChangeReceiver;
import com.network.internet.RegisterAndUnregisterNetworkReceiver;

public abstract class BaseActivity extends AppCompatActivity {

    private RegisterAndUnregisterNetworkReceiver registerAndUnregisterNetworkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerAndUnregisterNetworkReceiver= new RegisterAndUnregisterNetworkReceiver(BaseActivity.this);
        checkInternetConnection();
    }

    private void checkInternetConnection() {
        NetworkStateChangeReceiver.setNetworkStateChangeListener(new NetworkStateChangeListener() {
            @Override
            public void networkAvailable() {
                Toast.makeText(getApplicationContext(), "Now you are connected to Internet!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void networkUnavailable() {
                Toast.makeText(getApplicationContext(), "You are not connected to Internet!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume()
    {
        registerAndUnregisterNetworkReceiver.registerNetworkReceiver(BaseActivity.this);
        super.onResume();
    }
    @Override
    protected void onPause()
    {
        registerAndUnregisterNetworkReceiver.unregisterNetworkReceiver(BaseActivity.this);
        super.onPause();
    }
}
