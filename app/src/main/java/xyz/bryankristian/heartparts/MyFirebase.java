package xyz.bryankristian.heartparts;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bryanasakristian on 6/21/18.
 */

public class MyFirebase extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
