package com.example.geofences.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geofences.R;
import com.example.geofences.activities.HistoryActivity;
import com.example.geofences.adapters.HistoryAdapter;
import com.example.geofences.annotations.MyAnnotations;
import com.example.geofences.database.MyDatabase;
import com.example.geofences.interfaces.SelectAll;
import com.example.geofences.models.HistoryModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoneFragment extends Fragment implements SelectAll {
    RecyclerView recyclerView;
    View view;
    HistoryAdapter adapter;
    MyDatabase myDatabase;
    boolean isAll = true;
    private CheckBox checkBox;
    private TextView ss_tv;
    TextView noData_tv;
//    public DoneFragment() {
//        // Required empty public constructor
//    }

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
        AdView adView = view.findViewById(R.id.adView);
        adView(adView);
        noData_tv = view.findViewById(R.id.noData_tv);
        myDatabase = new MyDatabase(getActivity());
        checkBox = view.findViewById(R.id.checkBox);
        ss_tv = view.findViewById(R.id.selectDeSelect_tv);
        ArrayList<HistoryModel> list = ((HistoryActivity) getActivity()).getHistory(true);
        loadData(list);

        TextView textView = view.findViewById(R.id.delete_tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog(getActivity(), true);
            }
        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectAll selectAll = adapter.getSelectAll();
                if (isAll) {
                    selectAll.selected(false);
                    isAll = false;
                    ss_tv.setText("Select all");
                } else {
                    selectAll.selected(true);
                    isAll = true;
                    ss_tv.setText("De_Select all");
                }
            }
        });

        return view;
    }

    public void loadData(ArrayList<HistoryModel> list) {
        noData_tv = view.findViewById(R.id.noData_tv);
        recyclerView = view.findViewById(R.id.recyclerView);
        if (myDatabase == null) {
            myDatabase = new MyDatabase(getActivity());
        }

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        if (list.isEmpty()) {
            noData_tv.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.GONE);
            ss_tv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new HistoryAdapter(getActivity(), list, myDatabase, this, true);
            recyclerView.setLayoutManager(lm);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void selected(boolean selectedAll) {
        if (selectedAll) {
            checkBox.setChecked(true);
            isAll = true;
            ss_tv.setText("De_Select all");
        } else {
            checkBox.setChecked(false);
            isAll = false;
            ss_tv.setText("Select all");
        }
    }

    public void adView(final AdView adView) {

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError var1) {
                adView.setVisibility(View.GONE);
            }


        });
    }
    public void alertDialog(Context context, boolean isDone) {
        View view = getLayoutInflater().inflate(R.layout.delete_dialog_layout, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView no_ll = view.findViewById(R.id.no_tv);
        TextView yes_ll = view.findViewById(R.id.yes_tv);
        builder.setView(view).setCancelable(true);
        AlertDialog dialog = builder.create();
        if (adapter.getSendingList().isEmpty()) {
            Toast.makeText(context, "No item selected", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            no_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yes_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.deleteData();
                    ArrayList<HistoryModel> list = ((HistoryActivity) getActivity()).getHistory(true);
                    loadData(list);
                    dialog.dismiss();
                }
            });
        }
    }

}