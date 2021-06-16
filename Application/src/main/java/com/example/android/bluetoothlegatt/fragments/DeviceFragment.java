package com.example.android.bluetoothlegatt.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.SensorActivity;
import com.example.android.bluetoothlegatt.viewmodels.SensorViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceFragment extends Fragment {


    @BindView(R.id.read_switch) SwitchMaterial btnSensor;
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandableTextView;

    public DeviceFragment() {
        // Required empty public constructor
    }

    private SensorViewModel sensorVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);
        sensorVM = ((SensorActivity)getActivity()).getSensorVM();

        btnSensor.setOnCheckedChangeListener((buttonView, isChecked) -> sensorVM.readData());
        sensorVM.getSensorValue().observe(getActivity(), value ->{

        });
        return view;
    }
}
