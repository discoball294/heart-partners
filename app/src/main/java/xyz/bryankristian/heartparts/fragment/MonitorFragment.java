package xyz.bryankristian.heartparts.fragment;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.UUID;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import xyz.bryankristian.heartparts.FaqActivity;
import xyz.bryankristian.heartparts.History;
import xyz.bryankristian.heartparts.LoginActivity;
import xyz.bryankristian.heartparts.MainActivity;
import xyz.bryankristian.heartparts.customfonts.MyRegulerText;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.helpers.CustomBluetoothProfile;
import xyz.bryankristian.heartparts.helpers.MiBand2Helper;
import xyz.bryankristian.heartparts.services.MyService;


public class MonitorFragment extends Fragment implements MiBand2Helper.BLEAction {


    private View view;


    private CircleProgress circleProgress;
    private ArcProgress arcProgress;
    ImageView iconStatus;
    MiBand2Helper helper = null;
    Handler handler = new Handler(Looper.getMainLooper());
    Context sContext;
    PulsatorLayout pulsatorLayout;
    LinearLayout linearBtnHistory, linearBtnFaq;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MyRegulerText btnStop;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.clean_fragment, container, false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iconStatus = view.findViewById(R.id.icon_status);
        helper = new MiBand2Helper(sContext, handler);
        helper.addListener(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        helper.findBluetoothDevice(myBluetoothAdapter, "MI");
        helper.ConnectToGatt();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setupHeartBeat();

        btnStop = view.findViewById(R.id.btn_stop);

        linearBtnHistory = view.findViewById(R.id.btn_history);
        linearBtnFaq = view.findViewById(R.id.btn_faq);
        linearBtnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), History.class);
            startActivity(intent);
        });
        linearBtnFaq.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FaqActivity.class);
            startActivity(intent);
        });

        if (isMyServiceRunning(MyService.class))
            btnStop.setText("Stop Service");

        btnStop.setOnClickListener(v -> {
            if (isMyServiceRunning(MyService.class)){
                getActivity().stopService(new Intent(getActivity(), MyService.class));
                btnStop.setText("Start Service");
            }else {
                getActivity().startService(new Intent(getActivity(), MyService.class));
                btnStop.setText("Stop Service");
            }
        });



        arcProgress = view.findViewById(R.id.arc_progress);
        arcProgress.setProgress(sharedPreferences.getInt("averageHr", 0));
        arcProgress.setSuffixText("bpm");

        pulsatorLayout = (PulsatorLayout) view.findViewById(R.id.pulsator_arc);

        arcProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsatorLayout.start();
                //setupHeartBeat();
                getNewHeartBeat();
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void setHeartRateText(int heartRateText) {
        arcProgress.setProgress(heartRateText);

    }

    public void stopAnim() {
        pulsatorLayout.stop();
    }

    public void changeIconStatus(String status) {
        if (status.equals("sad")) {
            iconStatus.setImageResource(R.drawable.ic_sad);
            iconStatus.setColorFilter(ContextCompat.getColor(getActivity(), R.color.material_red_500));
        }
        else if (status.equals("normal"))
            iconStatus.setImageResource(R.drawable.ic_smile);
        iconStatus.setColorFilter(ContextCompat.getColor(getActivity(), R.color.material_green_500));

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

    public void getNewHeartBeat() {
        if (helper == null || !helper.isConnected()) {
            Toast.makeText(sContext, "Please setup first!", Toast.LENGTH_SHORT).show();
            return;
        }

        helper.writeData(
                CustomBluetoothProfile.UUID_SERVICE_HEARTBEAT,
                CustomBluetoothProfile.UUID_START_HEARTRATE_CONTROL_POINT,
                CustomBluetoothProfile.BYTE_NEW_HEART_RATE_SCAN
        );
    }
}
