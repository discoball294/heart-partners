package xyz.bryankristian.heartparts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
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

import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.User;


public class ProfileActivity extends Fragment {

    DatabaseReference databaseUser;
    FirebaseUser user;
    TextView scrollName, scrollTel, scrollEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme_NoActionBar);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        return localInflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        scrollName = view.findViewById(R.id.scroll_user_name);
        scrollTel = view.findViewById(R.id.scroll_user_tel);
        scrollEmail = view.findViewById(R.id.scroll_user_email);
        scrollEmail.setText(user.getEmail());
        readData();

    }

    private void readData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    scrollName.setText(user.getNama());
                    scrollTel.setText(user.getTelepon());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
