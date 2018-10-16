package xyz.bryankristian.heartparts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.bryankristian.heartparts.customfonts.MyEditText;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.HeartRate;
import xyz.bryankristian.heartparts.model.User;
import xyz.bryankristian.heartparts.model.Whitelist;

public class WhitelistActivity extends Fragment {

    DatabaseReference whitelistDB;
    DatabaseReference userDB;
    MyEditText emailText;
    RecyclerView whitelistRecycler;
    LinearLayoutManager linearLayoutManager;
    List<Whitelist> whitelists;
    FirebaseUser user;
    WhitelistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme_NoActionBar);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        return localInflater.inflate(R.layout.activity_whitelist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        whitelistRecycler = view.findViewById(R.id.whitelistRecycler);
        init();
        emailText = view.findViewById(R.id.text_search_email);
        emailText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                //Toast.makeText(getActivity(), "Done clicked", Toast.LENGTH_SHORT).show();
                search();
                return true;
            }
            return false;
        });
        getWhitelists();

    }

    public void init() {
        whitelistDB = FirebaseDatabase.getInstance().getReference("whitelists");
        userDB = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        whitelists = new ArrayList<>();
        whitelistRecycler.setHasFixedSize(true);
        whitelistRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void getWhitelists() {
        Query query = whitelistDB.orderByChild("userUID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                whitelists.clear();
                ArrayList<String> OSPlayerId = new ArrayList<String>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Whitelist model = dataSnapshot1.getValue(Whitelist.class);
                    OSPlayerId.add(model.getOSPlayerId());
                    whitelists.add(model);

                }
                Log.i("Array", Arrays.toString(OSPlayerId.toArray()));
                adapter = new WhitelistAdapter(whitelists, getContext());
                whitelistRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void search() {
        Query query = userDB.orderByChild("email").equalTo(emailText.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userData;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        userData = dataSnapshot1.getValue(User.class);
                        Whitelist whitelist = new Whitelist(user.getUid(),userData.getEmail(),userData.getNama(),userData.getTelepon(), userData.getOneSignalPlayerId());
                        showDialog(whitelist);
                    }


                } else {
                    Toast.makeText(getActivity(), "User tidak ada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void showDialog(Whitelist whitelist) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Add Whitelist")
                .setMessage("Are you sure you want to add this user as Whitelist?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Toast.makeText(getActivity(), "Ditambahkan", Toast.LENGTH_SHORT).show();
                    whitelistDB.push().setValue(whitelist);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .show();
    }
}
