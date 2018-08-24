package xyz.bryankristian.heartparts;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.HeartRate;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by bryanasakristian on 8/21/18.
 */



public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {
    private List<HeartRate> rateList = new ArrayList<>();
    private Context mContext;
    String TAG = "HR-TEXT";

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history, parent, false);
        return new ItemViewHolder(view);
    }

    public HistoryAdapter(Context mContext, List<HeartRate> rateList) {
        this.mContext = mContext;
        this.rateList = rateList;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        HeartRate history = rateList.get(position);
        String date = new SimpleDateFormat("dd MMM, yyyy HH:mm a")
                .format(new java.util.Date (history.getDate()));
        String status;
        String hrData = String.format(Locale.getDefault(),"%d", history.getHeartRate());
        Log.d(TAG, "onBindViewHolder: "+hrData);
        if ( history.getHeartRate() > 100){
            status = "Too High";
            holder.textStatus.setTextColor(ContextCompat.getColor(mContext, R.color.material_red_600));

        }else if (history.getHeartRate() < 60){
            status = "Too Low";
            holder.textStatus.setTextColor(ContextCompat.getColor(mContext, R.color.material_red_600));
        }else {
            status = "Normal";
            holder.textStatus.setTextColor(ContextCompat.getColor(mContext, R.color.material_green_600));
        }
        holder.textBeat.setText(hrData);
        holder.textStatus.setText(status);
        holder.textDate.setText(date);



    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textStatus, textBeat, textDate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textBeat = itemView.findViewById(R.id.text_beat);
            textStatus = itemView.findViewById(R.id.beat_status);
            textDate = itemView.findViewById(R.id.history_date);

        }
    }
}