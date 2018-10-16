package xyz.bryankristian.heartparts.services;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import xyz.bryankristian.heartparts.MainActivity;
import xyz.bryankristian.heartparts.fragment.MonitorFragment;
import xyz.bryankristian.heartparts.helpers.CustomBluetoothProfile;
import xyz.bryankristian.heartparts.helpers.MiBand2Helper;

/**
 * Created by bryanasakristian on 6/22/18.
 */

public class MyService extends Service implements MiBand2Helper.BLEAction {

    public static final long NOTIFY_INTERVAL = 6*10000;

    private Handler handler = new Handler();

    private Timer timer = null;

    MiBand2Helper helper = null;

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    String heartStatus;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new MiBand2Helper(this, handler);
        helper.addListener(this);

        helper.findBluetoothDevice(myBluetoothAdapter, "MI");
        helper.ConnectToGatt();
        if (timer!=null){
            timer.cancel();
        }else {
            timer = new Timer();
        }

        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    public void setupHeartBeat() {
        /*
        Steps to read heartbeat:
            - Register Notification (like in touch press)
                - Extra step with description
            - Write predefined bytes to control_point to trigger measurement
            - Listener will get result
        */

        if (helper != null)
            helper.getNotificationsWithDescriptor(
                    CustomBluetoothProfile.UUID_SERVICE_HEARTBEAT,
                    CustomBluetoothProfile.UUID_NOTIFICATION_HEARTRATE,
                    CustomBluetoothProfile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION
            );

        // Need to wait before first trigger, maybe something about the descriptor....
        /*
        Toast.makeText(MainActivity.this, "Wait for heartbeat setup...", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(5000,0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
    public void getNewHeartBeat()  {
        if (helper == null || !helper.isConnected()) {
            Toast.makeText(this, "Please setup first!", Toast.LENGTH_SHORT).show();
            return;
        }

        helper.writeData(
                CustomBluetoothProfile.UUID_SERVICE_HEARTBEAT,
                CustomBluetoothProfile.UUID_START_HEARTRATE_CONTROL_POINT,
                CustomBluetoothProfile.BYTE_NEW_HEART_RATE_SCAN
        );
    }

    class TimeDisplayTimerTask extends TimerTask{
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(MyService.this, "Invoked every 0.83 minutes", Toast.LENGTH_SHORT).show();
                    getNewHeartBeat();
                }
            });
        }

    }
}
