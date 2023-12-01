package com.example.motive_v1.Watch;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.Goal.GoalActivity;
import com.example.motive_v1.Home.HomeActivity;
import com.example.motive_v1.Mypage.MypageActivity;
import com.example.motive_v1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WatchActivity extends AppCompatActivity {

    // 이 부분에서 랜덤 UUID를 생성합니다.
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;

    private BluetoothSocket mmSocket;

    BottomNavigationView bottomNavigationView;
    private Button btn21;
    private Button btn22;
    private TextView connectTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            enableBluetooth();
        }

        btn21 = findViewById(R.id.btn21);
        btn22 = findViewById(R.id.btn22);
        connectTextView = findViewById(R.id.connectTextView);

        btn21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WatchActivity.this, WatchInfoActivity.class);
                startActivity(intent);
            }
        });

        btn22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WatchActivity.this, WatchAlarmActivity.class);
                startActivity(intent);
            }
        });

        connectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBluetoothDevice();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_watch);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(WatchActivity.this, HomeActivity.class);
                        break;
                    case R.id.menu_club:
                        intent = new Intent(WatchActivity.this, ClubActivity.class);
                        break;
                    case R.id.menu_goal:
                        intent = new Intent(WatchActivity.this, GoalActivity.class);
                        break;
                    case R.id.menu_watch:
                        intent = new Intent(WatchActivity.this, WatchActivity.class);
                        break;
                    case R.id.menu_profile:
                        intent = new Intent(WatchActivity.this, MypageActivity.class);
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Bluetooth를 지원하지 않는 기기
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enableBluetooth();
            } else {
                // 블루투스 권한이 거부된 경우
            }
        }
    }

    private void connectToDevice(BluetoothDevice device) {
        BluetoothSocket tmp = null;

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmSocket = tmp;
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }

    private void selectBluetoothDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                List<String> deviceNames = new ArrayList<>();
                final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
                for (BluetoothDevice device : devices) {
                    deviceNames.add(device.getName());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select a device")
                        .setItems(deviceNames.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                connectToDevice(devices[which]);
                            }
                        });
                builder.show();
            } else {
                // 페어링된 블루투스 디바이스가 없는 경우 처리
            }
        }
    }
}