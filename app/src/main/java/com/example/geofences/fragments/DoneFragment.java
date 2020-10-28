package com.example.geofences.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geofences.R;
import com.example.geofences.activities.HistoryActivity;
import com.example.geofences.adapters.HistoryAdapter;
import com.example.geofences.models.HistoryModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoneFragment extends Fragment {
    RecyclerView recyclerView;
    View view;

    public DoneFragment() {
        // Required empty public constructor
    }

    public static DoneFragment newInstance() {
        return new DoneFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_done, container, false);
        return view;
    }

    public void loadData(ArrayList<HistoryModel> list) {
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        HistoryAdapter adapter = new HistoryAdapter(getActivity(), list);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(((HistoryActivity) getActivity()).getHistory(true));

    }
}