package com.example.danielk.goalchamp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
public class CompletedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Goal> listGoals;


    public CompletedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        listGoals = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        mRecyclerView = view.findViewById(R.id.my_recycler_view);

        EditText search_Bar = view.findViewById(R.id.search_bar);
        search_Bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        final CompletedRvAdapter goalsRvAdapter = new CompletedRvAdapter(getContext(), listGoals);
        mRecyclerView.setAdapter(goalsRvAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    private void filter(String text) {
        ArrayList<Goal> filteredList = new ArrayList<>();
        for (Goal item : listGoals) {
            String alert = "Completed on " + item.getDate();
            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    item.getMessage().toLowerCase().contains(text.toLowerCase()) ||
                    alert.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        final CompletedRvAdapter goalsRvAdapter = new CompletedRvAdapter(getContext(), filteredList);
        mRecyclerView.setAdapter(goalsRvAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getFirebaseData(final GoalsCallback goalsCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("completedGoals").child(auth.getUid());

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

}
