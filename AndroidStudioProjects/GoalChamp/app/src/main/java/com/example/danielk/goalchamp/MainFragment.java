package com.example.danielk.goalchamp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danielk.goalchamp.AccountActivity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private RecyclerView myRecyclerView;
    private List<Goal> listGoals;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listGoals = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        myRecyclerView = view.findViewById(R.id.my_recycler_view);
        final GoalsRvAdapter goalsRvAdapter = new GoalsRvAdapter(getContext(), listGoals);
        myRecyclerView.setAdapter(goalsRvAdapter);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFirebaseData(new GoalsCallback() {
            @Override
            public void onCallBack(Goal goal) {
                listGoals.add(goal);
                goalsRvAdapter.notifyDataSetChanged();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void getFirebaseData(final GoalsCallback goalsCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("goals").child(auth.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Result will be hold here
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Goal goal = new Goal();
                    String noteId = String.valueOf(dataSnap.child("id").getValue());
                    String title = String.valueOf(dataSnap.child("title").getValue());
                    String message = String.valueOf(dataSnap.child("message").getValue());
                    String date = String.valueOf(dataSnap.child("date").getValue());
                    String time = String.valueOf(dataSnap.child("time").getValue());
                    goal.setId(noteId);
                    goal.setTitle(title);
                    goal.setMessage(message);
                    goal.setDate(date);
                    goal.setTime(time);
                    Log.d("test", noteId);
                    goalsCallback.onCallBack(goal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Handle Add Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //handle item selection
        if (id == R.id.addTask) {
            startActivity(new Intent(getActivity(), AddActivity.class));
        }
        return true;
    }
}
