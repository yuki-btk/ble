package com.example.android.bluetoothlegatt;

import android.accessibilityservice.AccessibilityService;
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
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;

/**
 * Created by yorke on 2018/02/12.
 * from https://qiita.com/araiyusuke/items/f37f88a9da6dc1989945
 */

public class NotificationService extends AccessibilityService {

    private String TAG = "Notification";
    final static String DEVICEADDRESS = "C3:E1:A3:56:8A:BD";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private static String mDeviceAddress=DEVICEADDRESS;

    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Messenger _messenger;

    private Toast mToast;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        String typeName = "";
        switch (type) {
            // Notificationの表示に変更があったとき
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                typeName = "TYPE_NOTIFICATION_STATE_CHANGED";
                BT_Connect_and_Led(mDeviceAddress);
                break;
            // View をスクロールしたとき
            // case AccessibilityEvent.TYPE_VIEW_SCROLLED:
            case AccessibilityEventCompat.TYPE_VIEW_SCROLLED:
                typeName = "TYPE_VIEW_SCROLLED";
                BT_Connect_and_Led(mDeviceAddress);
                break;
//            // View をタップしたとき
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                typeName = "TYPE_VIEW_CLICKED";
//                break;
//            // View にフォーカスがあたったとき
//            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
//                typeName = "TYPE_VIEW_FOCUSED";
//                break;
//            // View をロングタップしたとき
//            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
//                typeName = "TYPE_VIEW_LONG_CLICKED";
//                break;
//            // View が選択されたとき
//            case AccessibilityEvent.TYPE_VIEW_SELECTED:
//                typeName = "TYPE_VIEW_SELECTED";
//                break;
//            // View のテキストが変更されたとき
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                typeName = "TYPE_VIEW_TEXT_CHANGED";
//                break;
//            // 画面の表示に変更があったとき
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                typeName = "TYPE_WINDOW_STATE_CHANGED";
//                break;
//            // アナウンスがあったとき
//            // case AccessibilityEvent.TYPE_ANNOUNCEMENT:
//            case AccessibilityEventCompat.TYPE_ANNOUNCEMENT:
//                typeName = "TYPE_ANNOUNCEMENT";
//                break;
//            // ジェスチャーが終わったとき
//            // case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
//            case AccessibilityEventCompat.TYPE_GESTURE_DETECTION_END:
//                typeName = "TYPE_GESTURE_DETECTION_END";
//                break;
//            // ジェスチャーが始まったとき
//            // case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
//            case AccessibilityEventCompat.TYPE_GESTURE_DETECTION_START:
//                typeName = "TYPE_GESTURE_DETECTION_START";
//                break;
//            // タッチ探索が終わったとき
//            // case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
//            case AccessibilityEventCompat.TYPE_TOUCH_EXPLORATION_GESTURE_END:
//                typeName = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
//                break;
//            // タッチ探索が始まったとき
//            // case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
//            case AccessibilityEventCompat.TYPE_TOUCH_EXPLORATION_GESTURE_START:
//                typeName = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
//                break;
//            // タッチ操作が終わったとき
//            // case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
//            case AccessibilityEventCompat.TYPE_TOUCH_INTERACTION_END:
//                typeName = "TYPE_TOUCH_INTERACTION_END";
//                break;
//            // タッチ操作が始まったとき
//            // case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
//            case AccessibilityEventCompat.TYPE_TOUCH_INTERACTION_START:
//                typeName = "TYPE_TOUCH_INTERACTION_START";
//                break;
//            // View のアクセシビリティ・フォーカスがクリアされたとき
//            // case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
//            case AccessibilityEventCompat.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
//                typeName = "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
//                break;
//            // View がアクセシビリティ・フォーカスされたとき
//            // case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
//            case AccessibilityEventCompat.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
//                typeName = "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
//                break;
//            // View のホバーが始まったとき
//            // case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
//            case AccessibilityEventCompat.TYPE_VIEW_HOVER_ENTER:
//                typeName = "TYPE_VIEW_HOVER_ENTER";
//                break;
//            // View のホバーが終わったとき
//            // case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
//            case AccessibilityEventCompat.TYPE_VIEW_HOVER_EXIT:
//                typeName = "TYPE_VIEW_HOVER_EXIT";
//                break;

//            // View のテキスト範囲が変更されたとき
//            // case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//            case AccessibilityEventCompat.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                typeName = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
//                break;
//            // View のテキストを横断したとき
//            // case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
//            case AccessibilityEventCompat.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
//                typeName = "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
//                break;
//            // 画面内のコンテンツが変更されたとき
//            // case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//            case AccessibilityEventCompat.TYPE_WINDOW_CONTENT_CHANGED:
//                typeName = "TYPE_WINDOW_CONTENT_CHANGED";
//                break;
//            default:
//                typeName = "UNKNOWN_TYPE";
        }
//        if (mToast == null) {
//            mToast = Toast.makeText(getApplicationContext(), typeName, Toast.LENGTH_SHORT);
//        } else {
//            mToast.setText(typeName);
//        }
//        mToast.show();
    }

//    static class TestHandler extends Handler {
//        private Context _cont;
//
//        public TestHandler(Context cont) {
//            _cont = cont;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    mDeviceAddress = (String) msg.obj;
//                    //NotificationService ns = new NotificationService();
//                    //ns.BindBluetoothLe();
//            }
//        }
//    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // Serviceとの接続確立時に呼び出される。
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            BT_Connect_and_Led(mDeviceAddress);

//            mBluetoothLeService.connect(mDeviceAddress);
//            int counter = 0;
//            while((mGattCharacteristics.size() == 0) && counter < 50000){
//                GetCharacteristics(mBluetoothLeService.getSupportedGattServices());
//                counter++;
//            }
//            LedOn();
//            // call LedOff() after 1 second
//            Timer timer = new Timer();
//            TimerLedOff task = new TimerLedOff();
//            timer.schedule(task,1000);
        }

        public void onServiceDisconnected(ComponentName className) {
            // Serviceとの切断時に呼び出される。
            LedOff();
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        //_messenger = new Messenger(new TestHandler(getApplicationContext()));
        //mDeviceAddress ="C3:E1:A3:56:8A:BD";
        Intent itt = new Intent(this, BluetoothLeService.class);
        bindService(itt, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        LedOff();
    }

    @Override
    public void onInterrupt() {
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

    public void BT_Connect_and_Led(String DeviceAdress){
        mBluetoothLeService.connect(mDeviceAddress);
        int counter = 0;
        while((mGattCharacteristics.size() == 0) && counter < 50000){
            GetCharacteristics(mBluetoothLeService.getSupportedGattServices());
            counter++;
        }
        LedOn();
        // call LedOff() after 1 second
        Timer timer = new Timer();
        TimerLedOff task = new TimerLedOff();
        timer.schedule(task,1000);
    }

    public void BindBluetoothLe(){
        Intent itt = new Intent(this, BluetoothLeService.class);
        bindService(itt, mConnection, Context.BIND_AUTO_CREATE);
    }

}
