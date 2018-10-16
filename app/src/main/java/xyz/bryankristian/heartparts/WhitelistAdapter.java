package xyz.bryankristian.heartparts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.bryankristian.heartparts.heartpartners.R;
import xyz.bryankristian.heartparts.model.HeartRate;
import xyz.bryankristian.heartparts.model.User;
import xyz.bryankristian.heartparts.model.Whitelist;

public class WhitelistAdapter extends RecyclerView.Adapter<WhitelistAdapter.ItemViewHolder>{

    private List<Whitelist> whitelist = new ArrayList<>();
    private Context mContext;

    public WhitelistAdapter(List<Whitelist> whitelist, Context mContext) {
        this.whitelist = whitelist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_whitelist, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Whitelist whitelist1 = whitelist.get(position);
        holder.textNama.setText(whitelist1.getWhitelistName());
        holder.textEmail.setText(whitelist1.getWhitelistEmail());

    }

    @Override
    public int getItemCount() {
        return whitelist.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textNama, textEmail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textNama = itemView.findViewById(R.id.nama_list);
            textEmail = itemView.findViewById(R.id.email_list);

        }
    }
}
