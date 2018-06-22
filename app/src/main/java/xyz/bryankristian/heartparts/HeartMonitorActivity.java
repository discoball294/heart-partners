package xyz.bryankristian.heartparts;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import xyz.bryankristian.heartparts.heartpartners.R;

public class HeartMonitorActivity extends Fragment {

    ImageView heartPulse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.heart_monitor_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.heart_monitor_title);

        heartPulse = (ImageView) view.findViewById(R.id.heart_icon);

        final PulsatorLayout pulsatorLayout = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulsatorLayout.setCount(4);
        pulsatorLayout.setDuration(10000);
        heartPulse.setTag("off");

        heartPulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (heartPulse.getTag().toString().trim().equals("on")){
                    heartPulse.setTag("off");
                    pulsatorLayout.stop();
                }else if (heartPulse.getTag().toString().trim().equals("off")){
                    heartPulse.setTag("on");
                    pulsatorLayout.start();

                }


            }
        });
    }

}
