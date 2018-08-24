package xyz.bryankristian.heartparts;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import xyz.bryankristian.heartparts.fragment.MonitorFragment;
import xyz.bryankristian.heartparts.heartpartners.R;

public class MonitorActivity extends Fragment {


    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_monitor, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        frameLayout = (FrameLayout) view.findViewById(R.id.framelayout);


        replace_fragment(new MonitorFragment());

    }

    public void replace_fragment(Fragment fragment) {
        String fragmentTag = "frTag";
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment, fragmentTag);
        transaction.commit();
    }

}

