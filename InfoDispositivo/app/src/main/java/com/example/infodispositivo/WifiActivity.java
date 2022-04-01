package com.example.infodispositivo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.InetAddresses;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.infodispositivo.databinding.ActivityWifiBinding;

import java.net.InetAddress;
import java.util.Formatter;
import java.util.List;

public class WifiActivity extends AppCompatActivity {

    private static final boolean TODO = false;
    private MyBroadcastReceiver receiver;
    private ActivityWifiBinding binding;

    @SuppressLint("StaticFieldLeak")
    private static TextView wifi_status;
    private static TextView wifi_info;
    private static String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        binding = ActivityWifiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnIrBattery2.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        binding.btnIrWifi2.setOnClickListener(view -> {
            Intent intent = new Intent(this, WifiActivity.class);
            startActivity(intent);
        });

        binding.btnIrApp2.setOnClickListener(view -> {
            Intent intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        });


        //TODO: Monitorar a tela em primeiro plano
        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("TELA", "WIFI");
        startService(intent);

        wifi_status = findViewById(R.id.tvStatusWifi);
        wifi_info = findViewById(R.id.tvBssidWifi);
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkWifiOnAndConnected()) {
                status = "Ativado";
                Log.d("ABR", "O Status do Wi-Fi Mudou: " + status);
            } else {
                status = "Desativado";
                Log.d("ABR", "O Status do Wi-Fi Mudou: " + status);
            }
            wifi_status.setText("O Wi-Fi está " + status);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        int ip = wifiInfo.getIpAddress();
        int linkspeed = wifiInfo.getLinkSpeed();
        int networkId = wifiInfo.getNetworkId();
        //int seguranca = wifiInfo.getCurrentSecurityType();
        int rssi = wifiInfo.getRssi();
        //InetAddress.getByAddress(String.valueOf(ip));

        String info = "SSID: "+ssid +
                "\n\n" + "IP Address: " +ip +
                "\n\n" + "Link Speed: " +linkspeed +
                "\n\n" + "Network Id: " +networkId +
                "\n\n" + "RSSI: " +rssi;
        wifi_info.setText(info);




        if (wifiMgr.isWifiEnabled()) {
            return true;
        } else {
            return false;
        }
    }
}