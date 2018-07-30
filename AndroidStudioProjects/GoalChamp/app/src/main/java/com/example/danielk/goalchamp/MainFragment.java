package com.example.danielk.goalchamp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import android.widget.TableLayout;
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

    private ViewPager mViewPager;
    private TabLayout tabLayout;

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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        tabLayout = view.findViewById(R.id.tablayout_id);
        mViewPager = view.findViewById(R.id.viewpager_id);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        // add frag
        adapter.AddFragment(new UncompletedFragment(), "Ongoing");
        adapter.AddFragment(new CompletedFragment(), "Completed");
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
