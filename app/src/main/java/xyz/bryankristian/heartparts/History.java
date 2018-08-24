package xyz.bryankristian.heartparts;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.HeartRate;


public class History extends AppCompatActivity {

    @BindView(R.id.historyList)
    RecyclerView historyList;

    HistoryAdapter adapter;
    String TAG = "Recycler-Log";

    private FirebaseDatabase db;
    private DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser user;
    List<HeartRate> rateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        rateList = new ArrayList<>();
        historyList.setHasFixedSize(true);
        historyList.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("heartRate");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = reference.orderByChild("userUID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rateList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    HeartRate model = dataSnapshot1.getValue(HeartRate.class);
                    rateList.add(model);
                }
                adapter = new HistoryAdapter(History.this, rateList);
                historyList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");

            }
        });
    }



    private class GetDataFromFirebase extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }



}
