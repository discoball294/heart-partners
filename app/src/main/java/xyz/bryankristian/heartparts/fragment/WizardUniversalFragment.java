package xyz.bryankristian.heartparts.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.bryankristian.heartparts.heartpartners.R;

public class WizardUniversalFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private LinearLayout layout;
    private TextView icon, title, content;

    public static WizardUniversalFragment newInstance(int position) {
        WizardUniversalFragment f = new WizardUniversalFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_universal,
                container, false);
        layout = (LinearLayout) rootView
                .findViewById(R.id.fragment_wizard_universal_layout);
        title = (TextView) rootView
                .findViewById(R.id.fragment_wizard_universal_title);
        icon = (TextView) rootView
                .findViewById(R.id.fragment_wizard_universal_icon);
        content = (TextView) rootView
                .findViewById(R.id.fragment_wizard_universal_text);

        if (position == 0) {
            layout.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.material_red_300));
            layout.invalidate();
            icon.setText(R.string.material_icon_heart);
            title.setText(R.string.heart_monitor);
        } else if (position == 1) {
            layout.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.material_red_700));
            layout.invalidate();
            icon.setText(R.string.material_icon_alert_circle);
            title.setText(R.string.notify);
        } else {

            layout.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.material_red_900));
            layout.invalidate();
            icon.setText(R.string.material_icon_emergency);
            title.setText(R.string.in_case_emergency);
        }

        ViewCompat.setElevation(rootView, 50);
        return rootView;
    }

}