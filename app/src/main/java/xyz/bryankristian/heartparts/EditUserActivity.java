package xyz.bryankristian.heartparts;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import xyz.bryankristian.heartparts.customfonts.MyEditText;
import xyz.bryankristian.heartparts.customfonts.MyRegulerText;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.User;

public class EditUserActivity extends android.support.v4.app.Fragment {

    Calendar myCalendar;
    MyEditText textNama, textTempatLahir, textTanggalLahir, textAlamat, textTelepon, textBb;
    MyRegulerText btnSave;
    ProgressBar progressBar;
    private static final String TAG = "MainActivity";

    DatabaseReference databaseUser;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        return localInflater.inflate(R.layout.activity_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        textNama = (MyEditText)view.findViewById(R.id.text_nama);
        textTempatLahir = (MyEditText)view.findViewById(R.id.text_tempat_lahir);
        textAlamat = (MyEditText)view.findViewById(R.id.text_alamat);
        textTanggalLahir = (MyEditText) view.findViewById(R.id.text_tanggal_lahir);
        textTelepon = (MyEditText)view.findViewById(R.id.text_telepon);
        textBb = (MyEditText)view.findViewById(R.id.text_bb);
        btnSave = (MyRegulerText)view.findViewById(R.id.btn_save);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBarUser);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        textTanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.datepicker, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();

            }
        });
        readData();
    }

    private void saveUser(){
        String nama = textNama.getText().toString().trim();
        String tempatLahir = textTempatLahir.getText().toString();
        String tanggalLahir = textTanggalLahir.getText().toString();
        String alamat = textAlamat.getText().toString();
        String telepon = textTelepon.getText().toString();
        String bb= textBb.getText().toString();

        String id = user.getUid();

        if (TextUtils.isEmpty(nama)){
            Toast.makeText(getActivity(), "Masukkan nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tempatLahir)){
            Toast.makeText(getActivity(), "Masukkan tempat lahir", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tanggalLahir)){
            Toast.makeText(getActivity(), "Masukkan tanggal lahir", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(alamat)){
            Toast.makeText(getActivity(), "Masukkan alamat", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(telepon)){
            Toast.makeText(getActivity(), "Masukkan nomor telepon", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bb)){
            Toast.makeText(getActivity(), "Masukkan berat badan", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        User user = new User(nama, tempatLahir, tanggalLahir, alamat, telepon, bb);
        databaseUser.child(id).setValue(user);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Data disimpan", Toast.LENGTH_SHORT).show();


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
