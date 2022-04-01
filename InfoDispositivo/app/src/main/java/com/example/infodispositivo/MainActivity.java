package com.example.infodispositivo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.infodispositivo.databinding.ActivityMainBinding;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BatteryReceiver batteryReceiver;
    private BatteryReceiver batteryTimeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnIrBattery1.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        binding.btnIrWifi1.setOnClickListener(view -> {
            Intent intent = new Intent(this, WifiActivity.class);
            startActivity(intent);
        });

        binding.btnIrApp1.setOnClickListener(view -> {
            Intent intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        });

        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("TELA", "Bateria");
        startService(intent);

        loadBatterySection();
    }


    @Override
    protected void onStart() {
        super.onStart();

        batteryReceiver = new BatteryReceiver();
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter1);

        batteryTimeReceiver = new BatteryReceiver();
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryTimeReceiver, filter2);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }
        if (batteryTimeReceiver != null) {
            unregisterReceiver(batteryTimeReceiver);
            batteryTimeReceiver = null;
        }
    }

    public class BatteryReceiver extends BroadcastReceiver {

        //@RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onReceive(Context context, Intent intent) {


            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                float batteryPct = (level / (float)scale) * 100f;
                binding.tvnivelBateria.setText(batteryPct + "%");
            }


            BatteryManager batManager = (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
            long timeToCharge = 0L;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                timeToCharge = batManager.computeChargeTimeRemaining();
            }

            if (timeToCharge != -1){
                timeToCharge = timeToCharge/60000;
                Log.d("ABR", "Tempo restante de carregamento: " + Math.round(timeToCharge) + " Min");
                binding.tvTimeCharge.setText(Math.round(timeToCharge) + " Min");
            } else {
                binding.tvTimeCharge.setText(" -- ");
            }
        }
    }

    private void loadBatterySection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(batteryInfoReceiver, intentFilter);
    }
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryData(intent);
        }
    };
    private void updateBatteryData(Intent intent) {
        // display battery health
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (present) {
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    binding.tvHealth.setText("Cold");
                    break;

                case BatteryManager.BATTERY_HEALTH_DEAD:
                    binding.tvHealth.setText("Dead");
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    binding.tvHealth.setText("Good");
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    binding.tvHealth.setText("Over Voltage");
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    binding.tvHealth.setText("Overheat");
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    binding.tvHealth.setText("unspecified_failure");
                    break;

                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    binding.tvHealth.setText("Unknown");
                    break;
            }


            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    binding.tvCharging.setText("Wireless");
                    break;

                case BatteryManager.BATTERY_PLUGGED_USB:
                    binding.tvCharging.setText("USB");
                    break;

                case BatteryManager.BATTERY_PLUGGED_AC:
                    binding.tvCharging.setText("AC Charger");
                    break;

                default:
                    binding.tvCharging.setText("None");
                    break;
            }


            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    binding.tvStatus.setText("Charging");
                    break;

                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    binding.tvStatus.setText("Discharging");
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    binding.tvStatus.setText("Full");
                    break;

                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    binding.tvStatus.setText("Unknown");
                    break;

                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    binding.tvStatus.setText("Not Charging");
                    break;
            }

            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    binding.tvTechnology.setText(technology);
                }
            }

            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            if (temperature > 0) {
                int tempC = (int) temperature / 10;
                int tempF = (int) (tempC * 1.8 + 32);
                binding.tvTemperature.setText(tempC + "°C" + "/" + tempF + "°F");
            }

            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if (voltage > 0) {
                binding.tvVoltage.setText(voltage + " mV");
            }

            long capacity = getBatteryCapacity(this);

            if (capacity > 0) {
                binding.tvCapacity.setText(capacity + " mAh");
            }

            } else {
            binding.tvCapacity.setText("No Battery present");
        }
    }

    public long getBatteryCapacity(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
            Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }
        }
        return 0;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

