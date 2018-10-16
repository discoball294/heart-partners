package xyz.bryankristian.heartparts;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import xyz.bryankristian.heartparts.fragment.MonitorFragment;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.helpers.CustomBluetoothProfile;
import xyz.bryankristian.heartparts.helpers.MiBand2Helper;
import xyz.bryankristian.heartparts.model.HeartRate;
import xyz.bryankristian.heartparts.model.User;
import xyz.bryankristian.heartparts.model.Whitelist;
import xyz.bryankristian.heartparts.services.MyService;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements MiBand2Helper.BLEAction, NavigationView.OnNavigationItemSelectedListener {

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
    DatabaseReference whitelistDB;
    private FusedLocationProviderClient client;
    private String latLng = null;
    ArrayList<Integer> tenHr = new ArrayList<Integer>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String saved_nama, saved_telepon = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.app_name);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        heartRateDb = FirebaseDatabase.getInstance().getReference("heartRate");

        initMiBand();
        OneSignal.startInit(this)
                .setNotificationOpenedHandler((OneSignal.NotificationOpenedHandler) new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        headerTextEmail = (TextView) headerView.findViewById(R.id.header_textView_email);
        headerTextNama = (TextView) headerView.findViewById(R.id.header_text_name);
        if (user != null) {
            String userEmail = user.getEmail();
            OneSignal.sendTag("user_id", userEmail);
            headerTextEmail.setText(user.getEmail());
            Log.d(TAG, "email is " + user.getEmail());
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



        startService(new Intent(this, MyService.class));
        //showNotif("Normal","85");
        //sendNotification();


    }

    private void initMiBand() {
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

    private void readData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    headerTextNama.setText(user.getNama());
                    saved_nama = user.getNama();
                    saved_telepon = user.getTelepon();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNotif(String status, String heartRate) {
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
                .setContentText("Last 10 minutes Heart Rate Average is " + heartRate)
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_signout) {
            auth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(MainActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());

        return true;
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;

        switch (itemId) {
            case R.id.nav_home:
                fragment = new MonitorActivity();
                break;
            case R.id.nav_settings:
                fragment = new ProfileActivity();
                break;
            case R.id.nav_whitelists:
                fragment = new WhitelistActivity();
                break;
            case R.id.nav_chart:
                fragment = new ChartActivity();
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                tenHr.add((int) hearbeat);
                Log.i(TAG, "onNotification: menit ke "+tenHr.size());
                Double average = tenHr.stream().mapToInt(value -> value).average().orElse(0.0);
                if (tenHr.size() == sharedPreferences.getInt("interval",1)){
                    fr.setHeartRateText(average.intValue());
                    //Log.i(TAG, "onNotification: sudah sepuluh "+sharedPreferences.getAll());
                    editor.putInt("averageHr",average.intValue());
                    editor.apply();
                    tenHr.clear();
                    if (average.intValue() > 100) {
                        heartStatus = "Too High";
                        showDialog("Your Heartbeat too high");
                        fr.changeIconStatus("sad");
                    } else if (average.intValue() < 60) {
                        heartStatus = "Too Low";
                        showDialog("Your Heartbeat too low");
                        fr.changeIconStatus("sad");
                    } else {
                        heartStatus = "Normal";
                        fr.changeIconStatus("normal");
                    }
                    HeartRate hr = new HeartRate(System.currentTimeMillis(), average.intValue(), user.getUid());
                    heartRateDb.push().setValue(hr);

                    showNotif(heartStatus, String.valueOf(Math.round(average)));

                }
                //fr.stopAnim();
                Toast.makeText(MainActivity.this,
                        "Heartbeat: " + Byte.toString(hearbeat)
                        , Toast.LENGTH_SHORT).show();
            });
        } else if (alertUUID.equals(CustomBluetoothProfile.UUID_BUTTON_TOUCH)) {
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

    public void getNewHeartBeat() {
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

    public void showDialog(String title) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Arrhytmia detected, Notify whitelists in 30 seconds")
                .setNegativeButton(android.R.string.no, (dialog1, which) -> {
                    dialog1.dismiss();
                })
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                    Toast.makeText(MainActivity.this, "ok clicked", Toast.LENGTH_SHORT).show();
                    sendNotification();
                    dialog1.dismiss();
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 30000;

            @Override
            public void onShow(final DialogInterface dialog) {
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                final CharSequence positiveButtonText = defaultButton.getText();
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                positiveButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) //add one so it never displays zero
                        ));
                    }

                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            // TODO: positive action here
                            Toast.makeText(MainActivity.this, "ok clicked", Toast.LENGTH_SHORT).show();
                            sendNotification();
                            dialog.dismiss();
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    public void sendNotification() {
        AsyncTask.execute(() -> {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                return;
            }
            client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {


                    if(location!= null){
                        latLng = String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());
                        Log.i(TAG, "onSuccess: "+latLng);
                    }

                }
            });

            whitelistDB = FirebaseDatabase.getInstance().getReference("whitelists");
            Query query = whitelistDB.orderByChild("userUID").equalTo(user.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> OSPlayerId = new ArrayList<String>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Whitelist model = dataSnapshot1.getValue(Whitelist.class);
                        OSPlayerId.add(model.getOSPlayerId());
                    }

                    String playerId = new Gson().toJson(OSPlayerId);
                    Log.i("Array", playerId);
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                +   "\"app_id\": \"ff78cef8-a39e-433e-bfd0-d155d2ceb99e\","
                                +   "\"include_player_ids\": "+ playerId +","
                                +   "\"data\": {\"location\": \""+ latLng  +"\"},"
                                +   "\"small_icon\": \"ic_heart_pulse\","
                                +   "\"headings\": {\"en\": \""+ saved_nama +" maybe need help\"},"
                                +   "\"android_accent_color\": \""+ ContextCompat.getColor(MainActivity.this, R.color.material_red_500) +"\","
                                +   "\"contents\": {\"en\": \"Call or view location now\"},"
                                +   "\"buttons\": [{\"id\": \"call\", \"text\": \"Call\"},{\"id\": \"showloc\", \"text\": \"Show Direction\"}]"
                                + "}";


                        Log.i("strJsonBody: ", strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        Log.i("httpResponse: ", Integer.toString(httpResponse));

                        if (  httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        Log.i("jsonResponse:\n", jsonResponse);

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String latlng;
            Log.i(TAG, "notificationOpened: "+ data.optString("location"));


            if (actionType == OSNotificationAction.ActionType.ActionTaken){
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                if (result.action.actionID.equals("call")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+saved_telepon));
                    startActivity(intent);
                }else {
                    if (data != null) {
                        latlng = data.optString("location");
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr="+latlng));
                        startActivity(intent);
                    }
                }

            }


            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
        }
    }
}
