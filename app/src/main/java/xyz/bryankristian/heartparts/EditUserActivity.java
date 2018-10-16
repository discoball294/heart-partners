package xyz.bryankristian.heartparts;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import xyz.bryankristian.heartparts.customfonts.MyEditText;
import xyz.bryankristian.heartparts.customfonts.MyRegulerText;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.User;

public class EditUserActivity extends AppCompatActivity {

    Calendar myCalendar;
    MyEditText textNama, textTempatLahir, textTanggalLahir, textAlamat, textTelepon, textBb;
    MyRegulerText btnSave;
    ProgressBar progressBar;
    private static final String TAG = "MainActivity";

    DatabaseReference databaseUser;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        textNama = (MyEditText)findViewById(R.id.text_nama);
        textTempatLahir = (MyEditText)findViewById(R.id.text_tempat_lahir);
        textAlamat = (MyEditText)findViewById(R.id.text_alamat);
        textTanggalLahir = (MyEditText) findViewById(R.id.text_tanggal_lahir);
        textTelepon = (MyEditText)findViewById(R.id.text_telepon);
        textBb = (MyEditText)findViewById(R.id.text_bb);
        btnSave = (MyRegulerText)findViewById(R.id.btn_save);
        progressBar = (ProgressBar)findViewById(R.id.progressBarUser);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        textTanggalLahir.setOnClickListener(v -> new DatePickerDialog(EditUserActivity.this, R.style.datepicker, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        btnSave.setOnClickListener(v -> saveUser());
        readData();

    }





    private void saveUser(){
        OSPermissionSubscriptionState state = OneSignal.getPermissionSubscriptionState();
        Log.d(TAG, "onCreate: "+state.getSubscriptionStatus().getUserId());
        String nama = textNama.getText().toString().trim();
        String tempatLahir = textTempatLahir.getText().toString();
        String tanggalLahir = textTanggalLahir.getText().toString();
        String alamat = textAlamat.getText().toString();
        String telepon = textTelepon.getText().toString();
        String bb= textBb.getText().toString();

        String id = user.getUid();
        String email = user.getEmail();
        String oneSignalPlayerId = state.getSubscriptionStatus().getUserId();

        if (TextUtils.isEmpty(nama)){
            Toast.makeText(EditUserActivity.this, "Masukkan saved_nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tempatLahir)){
            Toast.makeText(EditUserActivity.this, "Masukkan tempat lahir", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tanggalLahir)){
            Toast.makeText(EditUserActivity.this, "Masukkan tanggal lahir", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(alamat)){
            Toast.makeText(EditUserActivity.this, "Masukkan alamat", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(telepon)){
            Toast.makeText(EditUserActivity.this, "Masukkan nomor telepon", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bb)){
            Toast.makeText(EditUserActivity.this, "Masukkan berat badan", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        User user = new User(nama, tempatLahir, tanggalLahir, alamat, telepon, bb, email, oneSignalPlayerId);
        databaseUser.child(id).setValue(user);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(EditUserActivity.this, "Data disimpan", Toast.LENGTH_SHORT).show();


    }

    private void readData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    textNama.setText(user.getNama());
                    textTempatLahir.setText(user.getTempatLahir());
                    textTanggalLahir.setText(user.getTanggalLahir());
                    textAlamat.setText(user.getAlamat());
                    textTelepon.setText(user.getTelepon());
                    textBb.setText(user.getBeratBadan());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateLabel(){
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textTanggalLahir.setText(sdf.format(myCalendar.getTime()));
    }
}
