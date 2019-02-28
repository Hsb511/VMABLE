package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class ShowSavedDevicesActivity extends Activity {

    private String mDeviceAddress = "D2:35:51:EB:E6:A2";
    private String mTerraillonAddress = "C4:BE:84:F7:97:E5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_devices);


        try {

            SharedPreferences sharedPref = getSharedPreferences("DeviceAddress", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(mDeviceAddress, "Emmanuel");
            editor.putString(mTerraillonAddress, "Yohann");
            editor.commit();
            Map<String, String> addresses = (Map<String, String>) sharedPref.getAll();

            for (Map.Entry<String, String> entry : addresses.entrySet())
            {
                showDevice(entry.getKey(), entry.getValue());
                Log.i("addresses", entry.getKey() + " : " + entry.getValue());
            }
        } catch (Exception e) {
            Log.i("addresses", "il y a eu une erreur : " + e.toString());
        }

    }

    protected void showDevice(String deviceAddress, String name) {
        LinearLayout devicesLayout = findViewById(R.id.deviceLayout);
        Log.i("addresses", devicesLayout.toString());
        LinearLayout deviceLayout = new LinearLayout(getApplicationContext());
        deviceLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView addressTV = new TextView(getApplicationContext());
        addressTV.setText(deviceAddress);
        deviceLayout.addView(addressTV);


        EditText nameET = new EditText(getApplicationContext());
        nameET.setText(name);
        deviceLayout.addView(nameET);

        Button addButton = new Button(getApplicationContext());
        addButton.setText("+");
        deviceLayout.addView(addButton);

        devicesLayout.addView(deviceLayout);


    }
}
