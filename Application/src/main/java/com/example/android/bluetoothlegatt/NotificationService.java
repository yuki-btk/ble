package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yorke on 2018/02/12.
 * from https://qiita.com/araiyusuke/items/f37f88a9da6dc1989945
 */

public class NotificationService extends NotificationListenerService {

    private String TAG = "Notification";


    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private static String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Messenger _messenger;

    static class TestHandler extends Handler {
        private Context _cont;

        public TestHandler(Context cont) {
            _cont = cont;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mDeviceAddress = (String) msg.obj;
                    //NotificationService ns = new NotificationService();
                    //ns.BindBluetoothLe();
            }
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // Serviceとの接続確立時に呼び出される。
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);


            int counter = 0;
            while((mGattCharacteristics.size() == 0) && counter < 50000){
                //timer.schedule(task, 500);
                GetCharacteristics(mBluetoothLeService.getSupportedGattServices());
                counter++;
            }
            LedOn();

            // call LedOff() after 1 second
            Timer timer = new Timer();
            TimerLedOff task = new TimerLedOff();
            timer.schedule(task,1000);
        }

        public void onServiceDisconnected(ComponentName className) {
            // Serviceとの切断時に呼び出される。
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        //mDeviceName = intent.getStringExtra("name");
        mDeviceAddress =intent.getStringExtra("address");
        //String hoge=mDeviceAddress;
        //return super.onBind(intent);
        //return mBinder;
        Intent itt = new Intent(this, BluetoothLeService.class);
        bindService(itt, mConnection, Context.BIND_AUTO_CREATE);
        return _messenger.getBinder();
    }

    //private final IBinder mBinder = new Binder();

//    private final IBinder mBinder = new LocalBinder();
//
//    public class LocalBinder extends Binder {
//        NotificationService getService(){
//            return NotificationService.this;
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        _messenger = new Messenger(new TestHandler(getApplicationContext()));
        //String hoge = mDeviceAddress;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //通知が更新
        LedOn();

        // call LedOff() after 1 second
        Timer timer = new Timer();
        TimerLedOff task = new TimerLedOff();
        timer.schedule(task,1000);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //通知が削除
        LedOn();

        // call LedOff() after 1 second
        Timer timer = new Timer();
        TimerLedOff task = new TimerLedOff();
        timer.schedule(task,1000);
    }

    private void GetCharacteristics(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 }
//        );
//        mGattServicesList.setAdapter(gattServiceAdapter);
    }


    private void LedOn(){
        //test
        byte[] data = new byte[]{(byte) 0x01};
        BluetoothGattCharacteristic ch;
        for (int i = 0; i < mGattCharacteristics.size(); i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size(); j++) {
                //Log.d(TAG, "row: " + mGattCharacteristics.size());
                //Log.d(TAG, "col: " + mGattCharacteristics.get(i).size());
                ch = mGattCharacteristics.get(i).get(j);
                mBluetoothLeService.writeCharacteristic(ch, data);
            }
        }
    }

    private void LedOff(){
        //test
        byte[] data = new byte[]{(byte) 0x00};
        BluetoothGattCharacteristic ch;
        for (int i = 0; i < mGattCharacteristics.size(); i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size(); j++) {
                //Log.d(TAG, "row: " + mGattCharacteristics.size());
                //Log.d(TAG, "col: " + mGattCharacteristics.get(i).size());
                ch = mGattCharacteristics.get(i).get(j);
                mBluetoothLeService.writeCharacteristic(ch, data);
            }
        }
    }
    private class TimerLedOff extends TimerTask {

        @Override
        public void run() {
            // This method is called once the time is elapsed
            LedOff();
        }
    }

    public void BindBluetoothLe(){
        Intent itt = new Intent(this, BluetoothLeService.class);
        bindService(itt, mConnection, Context.BIND_AUTO_CREATE);
    }

}
