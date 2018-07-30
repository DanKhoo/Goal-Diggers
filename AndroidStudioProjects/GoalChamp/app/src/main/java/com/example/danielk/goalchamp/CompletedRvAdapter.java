package com.example.danielk.goalchamp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CompletedRvAdapter extends RecyclerView.Adapter<CompletedRvAdapter.MyViewHolder> {

    Context mContext;
    List<Goal> mData;
    Dialog myDialog;
    String goalId;

    public CompletedRvAdapter(Context mContext, List<Goal> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_goal, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        // Dialog ini
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.dialog_complete);

        vHolder.item_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dialog_title_tv = myDialog.findViewById(R.id.dialog_title);
                TextView dialog_message_tv = myDialog.findViewById(R.id.dialog_message);
                Button dialog_delete = myDialog.findViewById(R.id.dialog_btn_delete);
                dialog_title_tv.setText(mData.get(vHolder.getAdapterPosition()).getTitle());
                dialog_message_tv.setText(mData.get(vHolder.getAdapterPosition()).getMessage());
                goalId = mData.get(vHolder.getAdapterPosition()).getId();
                dialog_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog();
                    }
                });
                myDialog.show();
            }
        });
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_message.setText(mData.get(position).getMessage());
        holder.tv_alarm.setText("Completed on " + mData.get(position).getDate());
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder((mContext));
        builder.setMessage("Delete this Goal?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGoal();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteGoal() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("completedGoals").child(auth.getUid());
        mDatabase.child(goalId).removeValue();
        Toast.makeText(mContext, "Goal deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {return mData.size(); }

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
