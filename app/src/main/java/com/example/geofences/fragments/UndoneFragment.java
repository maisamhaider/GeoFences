package com.example.geofences.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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
import com.example.geofences.database.MyDatabase;
import com.example.geofences.interfaces.SelectAll;
import com.example.geofences.models.HistoryModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UndoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UndoneFragment extends Fragment implements SelectAll {
    RecyclerView recyclerView;
    View view;
    MyDatabase myDatabase;
    HistoryAdapter adapter;
    boolean isAll = false;
    CheckBox checkBox;
    TextView ss_tv;
    TextView noData_tv;
//    public UndoneFragment() {
//        // Required empty public constructor
//    }


    // TODO: Rename and change types and number of parameters
    public static UndoneFragment newInstance() {
        return new UndoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_undone, container, false);
        AdView adView = view.findViewById(R.id.adView);
        adView(adView);
        noData_tv = view.findViewById(R.id.noData_tv);
        myDatabase = new MyDatabase(getActivity());
        checkBox = view.findViewById(R.id.checkBox);
        ss_tv = view.findViewById(R.id.selectDeSelect_tv);
        TextView textView = view.findViewById(R.id.delete_tv);
        ArrayList<HistoryModel> list = ((HistoryActivity) getActivity()).getHistory(false);
        loadData(list);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog(getActivity(), false);
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
        if (myDatabase == null) {
            myDatabase = new MyDatabase(getActivity());
        }
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        if (list.isEmpty()) {
            noData_tv.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.GONE);
            ss_tv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            adapter = new HistoryAdapter(getActivity(), list, myDatabase, this, false);

            recyclerView.setVisibility(View.VISIBLE);
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
                    ArrayList<HistoryModel> list =new ArrayList<> ();
                    list = ((HistoryActivity)
                            getActivity()).getHistory(false);
                    loadData(list);
                    dialog.dismiss();
                }
            });
        }
    }
}