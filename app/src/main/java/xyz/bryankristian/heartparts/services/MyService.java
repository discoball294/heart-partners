package xyz.bryankristian.heartparts.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bryanasakristian on 6/22/18.
 */

public class MyService extends Service {

    public static final long NOTIFY_INTERVAL = 10*1000;

    private Handler handler = new Handler();

    private Timer timer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (timer!=null){
            timer.cancel();
        }else {
            timer = new Timer();
        }

        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    class TimeDisplayTimerTask extends TimerTask{
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyService.this, "Invoked every 10 seconds", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
