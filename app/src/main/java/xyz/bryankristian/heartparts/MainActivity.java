package xyz.bryankristian.heartparts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import xyz.bryankristian.heartparts.fragment.MonitorFragment;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.helpers.CustomBluetoothProfile;
import xyz.bryankristian.heartparts.helpers.MiBand2Helper;
import xyz.bryankristian.heartparts.model.HeartRate;
import xyz.bryankristian.heartparts.model.User;
import xyz.bryankristian.heartparts.services.MyService;

public class MainActivity extends AppCompatActivity
        implements MiBand2Helper.BLEAction,NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    public FirebaseUser user;
    private boolean isAuthListenerSet = false;
    private TextView headerTextEmail, headerTextNama;
    private static final String TAG = "MainActivity";
    private View headerView;
    private NavigationView navigationView;
    private FirebaseDatabase database;
    MiBand2Helper helper = null;
    Handler handler = new Handler(Looper.getMainLooper());
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    String heartStatus;
    DatabaseReference heartRateDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.app_name);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        heartRateDb = FirebaseDatabase.getInstance().getReference("heartRate");

        initMiBand();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        headerTextEmail = (TextView)headerView.findViewById(R.id.header_textView_email);
        headerTextNama = (TextView)headerView.findViewById(R.id.header_text_name);
        if (user!=null){
            headerTextEmail.setText(user.getEmail());
            Log.d(TAG,"email is "+user.getEmail());
            readData();
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_home);

        //startService(new Intent(this, MyService.class));
        //showNotif("Normal","85");

    }

    private void initMiBand(){
        helper = new MiBand2Helper(MainActivity.this, handler);
        helper.addListener(this);

        helper.findBluetoothDevice(myBluetoothAdapter, "MI");
        helper.ConnectToGatt();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setupHeartBeat();
    }

    private void readData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null){
                    headerTextNama.setText(user.getNama());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNotif(String status, String heartRate){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 50, 100});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_heart_pulse)
                .setTicker("Hearty365")
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(status)
                .setContentText("Current Heart Rate is "+heartRate)
                .setContentInfo("Info")
                .setOngoing(true)
                .setColor(ContextCompat.getColor(MainActivity.this, R.color.material_red_500))
                .setVibrate(new long[]{0, 100, 50, 100});



        notificationManager.notify(/*notification id*/1, notificationBuilder.build());

    }

    final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_signout){
            auth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());

        return true;
    }

    private void displaySelectedScreen(int itemId){
        Fragment fragment = null;

        switch (itemId){
            case R.id.nav_home:
                fragment = new MonitorActivity();
                break;
            case R.id.nav_settings:
                fragment = new ProfileActivity();
                break;

        }

        if (fragment!=null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isAuthListenerSet) {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
            isAuthListenerSet = true;
        }
        //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
            isAuthListenerSet = false;
        }
        //Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "state: connected");
    }

    @Override
    public void onConnect() {
        Log.d(TAG, "state: disconnected");
    }

    @Override
    public void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        UUID alertUUID = characteristic.getUuid();
        if (alertUUID.equals(CustomBluetoothProfile.UUID_NOTIFICATION_HEARTRATE)) {
            final byte hearbeat =
                    characteristic.getValue()[1];
            final int heartRate = Integer.parseInt(Byte.toString(hearbeat));

            handler.post(() -> {
                MonitorFragment fr = (MonitorFragment) getSupportFragmentManager().findFragmentByTag("frTag");
                fr.setHeartRateText(Byte.toString(hearbeat));
                if ( heartRate > 100){
                    heartStatus = "Too High";
                }else if (heartRate < 60){
                    heartStatus = "Too Low";
                }else {
                    heartStatus = "Normal";
                }

                HeartRate hr = new HeartRate(System.currentTimeMillis(),heartRate,user.getUid());
                heartRateDb.push().setValue(hr);

                showNotif(heartStatus,Byte.toString(hearbeat));
                Toast.makeText(MainActivity.this,
                        "Heartbeat: " + Byte.toString(hearbeat)
                        , Toast.LENGTH_SHORT).show();

            });
        }
        else if (alertUUID.equals(CustomBluetoothProfile.UUID_BUTTON_TOUCH)) {
            handler.post(() -> {
                getNewHeartBeat();
                Toast.makeText(MainActivity.this,
                        "Button Press! "
                        , Toast.LENGTH_SHORT).show();
            });
        }
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
            Toast.makeText(MainActivity.this, "Please setup first!", Toast.LENGTH_SHORT).show();
            return;
        }

        helper.writeData(
                CustomBluetoothProfile.UUID_SERVICE_HEARTBEAT,
                CustomBluetoothProfile.UUID_START_HEARTRATE_CONTROL_POINT,
                CustomBluetoothProfile.BYTE_NEW_HEART_RATE_SCAN
        );
    }
}
