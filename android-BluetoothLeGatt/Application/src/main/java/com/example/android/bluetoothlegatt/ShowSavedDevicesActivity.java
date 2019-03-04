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

import java.util.Map;

public class ShowSavedDevicesActivity extends Activity {

    private final static int CHECK_DEVICE = 2323;
    public final static int ADD_DEVICE = 2346;
    public final static int DONT_ADD_DEVICE = 2300;
    public final static String DEVICE_ADDRESS_BACK = "DEVICE_ADDR_BACK";

    private String mDeviceAddress = "D2:35:51:EB:E6:A2";
    private String mTerraillonAddress = "C4:BE:84:F7:97:E5";
    private LinearLayout devicesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_devices);

        devicesLayout = findViewById(R.id.device_layout);


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
                final Intent intent = new Intent(ShowSavedDevicesActivity.this, DeviceCheckActivity.class);
                intent.putExtra(DeviceCheckActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(DeviceCheckActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.putExtra(DeviceCheckActivity.EXTRAS_RUNNERS_NAME, runnersName);
                startActivityForResult(intent, CHECK_DEVICE);
            }
        });
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
        if (requestCode == CHECK_DEVICE) {
            String deviceAddress = data.getStringExtra(DEVICE_ADDRESS_BACK);
            Button buttonState = getStateButtonByAddress(deviceAddress);
            if (resultCode == ADD_DEVICE) {
                buttonState.setBackgroundColor(getResources().getColor(R.color.nice_green));
            } else if (resultCode == DONT_ADD_DEVICE) {
                buttonState.setBackgroundColor(getResources().getColor(R.color.nice_red));
            }
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


    protected boolean isInSharedPref(String sharedPrefName) {
        boolean itsin = false;
        return itsin;
    }
}
