package com.example.danielk.goalchamp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GoalsRvAdapter extends RecyclerView.Adapter<GoalsRvAdapter.MyViewHolder> {

    Context mContext;
    List<Goal> mData;

    public GoalsRvAdapter(Context mContext, List<Goal> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_goal, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        vHolder.item_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Goal goal = mData.get(vHolder.getAdapterPosition());
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("goal", goal);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_message.setText(mData.get(position).getMessage());
        Log.d("DATE",mData.get(position).getDate());
        if (mData.get(position).getDate().equals("Not Set")) {
            holder.tv_alarm.setText("No Alert.");
        } else {
            holder.tv_alarm.setText("Alert on " + mData.get(position).getDate() + " " + mData.get(position).getTime());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item_goal;
        private TextView tv_title, tv_message, tv_alarm;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.recycle_title);
            tv_message = itemView.findViewById(R.id.recycle_message);
            item_goal = itemView.findViewById(R.id.goal_item_id);
            tv_alarm = itemView.findViewById(R.id.recycle_alarm);
        }
    }
}
