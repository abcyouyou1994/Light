package com.qdjk.bluet.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private Button openblt, scandevice;
    private TextView scan_state;
    private BluetoothAdapter bltAdapter;
    private ArrayList<String> mArrayAdapter = new ArrayList<>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private ListView bltview;//ListView组件
    private ArrayAdapter<String> mAdapter;

    private BluetoothDevice bltdevice;


//广播回调
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                devices.add(device);
                mAdapter.notifyDataSetChanged();


            }
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_state = (TextView) findViewById(R.id.scan_state);
        openblt = (Button) findViewById(R.id.openBlt);
        scandevice = (Button) findViewById(R.id.scan_device);
        bltview = (ListView) findViewById(R.id.bltview);
        openblt.setOnClickListener(this);
        scandevice.setOnClickListener(this);
        bltview.setOnItemClickListener(this);
        bltAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mArrayAdapter);
        bltview.setAdapter(mAdapter);
        bltview.setOnItemClickListener(this);



    }
    //回调


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openBlt:
                //打开蓝牙
                checkBluetooth(v);
                break;
            case R.id.scan_device:
                //扫描设备
                scanBluetooth(v);
                break;
        }
    }

    @SuppressLint("WrongConstant")
    private void scanBluetooth(View v) {
        if (bltAdapter.isEnabled() && !bltAdapter.isDiscovering()) {
            boolean startDiscovery = bltAdapter.startDiscovery();

            if (!startDiscovery) {
                Toast.makeText(this, "开始扫描失败！", 0).show();
                return;
            }
            Toast.makeText(this, "开始扫描", 0).show();
            mArrayAdapter.clear();
            devices.clear();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
        } else {
            String scan_state1 = "正在扫描";
            scan_state.setText(scan_state1);
            unregisterReceiver(receiver);
        }
    }

    @SuppressLint("WrongConstant")
    private void checkBluetooth(View v) {
        if (bltAdapter == null) {
            Toast.makeText(this, "对不起，您的设备不支持蓝牙！", 0).show();
            return;
        }
        boolean enabled = bltAdapter.isEnabled();
        if (!enabled) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        queryingPairedDevices();
    }

    @SuppressLint("WrongConstant")
    private void queryingPairedDevices() {
        mArrayAdapter.clear();
        devices = new ArrayList<>(bltAdapter.getBondedDevices());
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "没有找到已经配对的蓝牙", 0).show();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(MainActivity.this, communityBLT.class);
        intent.putExtra("device", devices.get(position));
        bltAdapter.cancelDiscovery();
        startActivity(intent);


    }

    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(receiver);
    }


    public static byte[] hexString2Byte(String s) {
        int len = (s.length() / 2);
        byte[] result = new byte[len];
        char[] achar = s.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) ((toByte(achar[pos]) << 4 | toByte(achar[pos + 1])) & 0xff);
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789abcdef".indexOf(c);
        return b;
    }

}