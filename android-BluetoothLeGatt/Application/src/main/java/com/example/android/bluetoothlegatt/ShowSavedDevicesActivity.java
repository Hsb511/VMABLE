package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ShowSavedDevicesActivity extends Activity {

    private final static int CHECK_DEVICE = 2323;
    public final static int ADD_DEVICE = 2346;
    public final static int DONT_ADD_DEVICE = 2300;
    public final static String DEVICE_ADDRESS_BACK = "DEVICE_ADDR_BACK";
    public final static String RUNNERS_NAME_BACK = "RUNNERS_NAME_BACK";
    public final static String ADDED_DEVICES_PREF = "ADDED_DEVICES";
    public final static String NOT_ADDED_DEVICES_PREF = "NOT_ADDED_DEVICES";

    private String mDeviceAddress = "D2:35:51:EB:E6:A2";
    private String mTerraillonAddress = "C4:BE:84:F7:97:E5";
    private String mJulienAddress = "F7:BF:8B:59:98:B9";
    private LinearLayout devicesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_devices);

        devicesLayout = findViewById(R.id.deviceLayout);
        devicesLayout.removeAllViews();
        showSavedDevices("", "");
    }

    protected void showSavedDevices(String deviceToUpdate, String nameToUpdate) {
        devicesLayout.removeAllViews();
        try {
            SharedPreferences sharedPref = getSharedPreferences("DeviceAddress", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(mDeviceAddress, "Emmanuel");
            editor.putString(mTerraillonAddress, "Yohann");
            editor.putString(mJulienAddress, "Julien");
            editor.commit();
            Map<String, String> addresses = (Map<String, String>) sharedPref.getAll();

            for (Map.Entry<String, String> entry : addresses.entrySet())
            {
                Log.i("addresses", entry.getKey() + " : " + entry.getValue());
                String runnersName = entry.getValue();


                if (entry.getKey().equals(deviceToUpdate)) {
                    runnersName = nameToUpdate;
                    editor.putString(entry.getKey(), runnersName);
                    editor.commit();

                }
                showDevice(entry.getKey(), runnersName);
            }
        } catch (Exception e) {
            Log.i("addresses", "il y a eu une erreur : " + e.toString());
        }

    }

    protected void showDevice(final String deviceAddress, String name) {
        devicesLayout = findViewById(R.id.deviceLayout);
        Log.i("addresses", devicesLayout.toString());
        LinearLayout deviceLayout = new LinearLayout(getApplicationContext());
        deviceLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView addressTV = new TextView(getApplicationContext());
        addressTV.setText(deviceAddress);
        deviceLayout.addView(addressTV);


        final String runnersName = name;
        EditText nameET = new EditText(getApplicationContext());
        nameET.setText(name);
        deviceLayout.addView(nameET);

        Button addButton = new Button(getApplicationContext());
        addButton.setText("+");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                Intent intent = new Intent(ShowSavedDevicesActivity.this, DeviceCheckActivity.class);
                if (getIntent().getStringExtra("Activity").equals("Scan")) {
                    intent = new Intent(ShowSavedDevicesActivity.this, DeviceControlActivity.class);
                }
                intent.putExtra(DeviceCheckActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(DeviceCheckActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.putExtra(DeviceCheckActivity.EXTRAS_RUNNERS_NAME, runnersName);
                //startActivityForResult(intent, CHECK_DEVICE);
                startActivity(intent);
            }
        });
        if (isInSharedPref(ADDED_DEVICES_PREF, deviceAddress)) {
            addButton.setBackgroundColor(getResources().getColor(R.color.nice_green));
        } else if (isInSharedPref(NOT_ADDED_DEVICES_PREF, deviceAddress)) {
            addButton.setBackgroundColor(getResources().getColor(R.color.nice_red));
        }
        deviceLayout.addView(addButton);
        devicesLayout.addView(deviceLayout);

        findViewById(R.id.startRaceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences sharedPrefA = getSharedPreferences("AddedDevices", MODE_PRIVATE);
                    SharedPreferences.Editor editorA = sharedPrefA.edit();
                    editorA.clear();
                    editorA.commit();
                    SharedPreferences sharedPrefD = getSharedPreferences("NotAddedDevices", MODE_PRIVATE);
                    SharedPreferences.Editor editorD = sharedPrefD.edit();
                    editorD.clear();
                    editorD.commit();
                } catch (Exception e) {
                    Log.i("addresses", "il y a eu une erreur : " + e.toString());
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("testD", "onActivityResult");
        if (requestCode == CHECK_DEVICE) {
            String deviceAddress = data.getStringExtra(DEVICE_ADDRESS_BACK);
            Log.i("testD", "get device address : " + deviceAddress);
            String runnersName = data.getStringExtra(RUNNERS_NAME_BACK);
            Log.i("testD", "get new Runner's name : " + runnersName);
            if (resultCode == ADD_DEVICE) {
                SharedPreferences sharedPrefA = getSharedPreferences(ADDED_DEVICES_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editorA = sharedPrefA.edit();
                editorA.putString(deviceAddress, runnersName);
                editorA.commit();
                Log.i("testD", sharedPrefA.getAll().toString());
                Log.i("testD", "is in deleted device : " + String.valueOf(isInSharedPref(NOT_ADDED_DEVICES_PREF, deviceAddress)));
                if (isInSharedPref(NOT_ADDED_DEVICES_PREF, deviceAddress)) {
                    removeFromPref(NOT_ADDED_DEVICES_PREF, deviceAddress);
                }

            } else if (resultCode == DONT_ADD_DEVICE) {
                SharedPreferences sharedPrefN = getSharedPreferences(NOT_ADDED_DEVICES_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editorN = sharedPrefN.edit();
                editorN.putString(deviceAddress, runnersName);
                editorN.commit();
                Log.i("testD", sharedPrefN.getAll().toString());
                Log.i("testD", "is in added device : " + String.valueOf(isInSharedPref(ADDED_DEVICES_PREF, deviceAddress)));
                if (isInSharedPref(ADDED_DEVICES_PREF, deviceAddress)) {
                    removeFromPref(ADDED_DEVICES_PREF, deviceAddress);
                }
            }
            showSavedDevices(deviceAddress, runnersName);

        }
    }

    protected Button getStateButtonByAddress(String deviceAddress) {
        Button deviceButton = new Button(getApplicationContext());
        for (int i=0; i < devicesLayout.getChildCount(); i++) {
            LinearLayout deviceLayout = (LinearLayout) devicesLayout.getChildAt(i);
            TextView addressTV = (TextView) deviceLayout.getChildAt(0);
            if (addressTV.getText().toString().equals(deviceAddress)) {
                deviceButton = (Button) deviceLayout.getChildAt(2);
                break;
            }
        }

        return deviceButton;
    }


    protected boolean isInSharedPref(String sharedPrefName, String deviceAddress) {
        boolean itsin = false;
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefName, MODE_PRIVATE);
        Map<String, String> addresses = (Map<String, String>) sharedPref.getAll();
        for (Map.Entry<String, String> entry : addresses.entrySet())
        {
            if (entry.getKey().equals(deviceAddress)) itsin = true;
        }
        return itsin;
    }

    protected void removeFromPref(String sharedPrefName, String deviceAddress) {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(deviceAddress);
        editor.commit();
    }

}
