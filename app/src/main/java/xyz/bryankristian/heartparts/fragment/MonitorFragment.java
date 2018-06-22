package xyz.bryankristian.heartparts.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.CircleProgress;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import xyz.bryankristian.heartparts.heartpartners.R;


public class MonitorFragment extends Fragment {


    private View view;


    private CircleProgress circleProgress;
    private ArcProgress arcProgress;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.clean_fragment, container, false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arcProgress = view.findViewById(R.id.arc_progress);

        arcProgress.setProgress(110);
        arcProgress.setSuffixText("bpm");

        final PulsatorLayout pulsatorLayout = (PulsatorLayout) view.findViewById(R.id.pulsator_arc);
        pulsatorLayout.stop();
    }
}
