package com.example.android.bluetoothlegatt.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.ScannerActivity;
import com.example.android.bluetoothlegatt.viewmodels.DevicesLiveData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private List<DiscoveredBluetoothDevice> devices;
    private OnItemClickListener onItemClickListener;

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(@NonNull final DiscoveredBluetoothDevice device);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public DevicesAdapter(@NonNull final ScannerActivity activity,
                          @NonNull final DevicesLiveData devicesLiveData) {
        setHasStableIds(true);

        devicesLiveData.observe(activity, newDevices -> {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                    new DeviceDiffCallback(devices, newDevices), false);
            devices = newDevices;
            result.dispatchUpdatesTo(this);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DiscoveredBluetoothDevice device = devices.get(position);
        final String deviceName = device.getName();

        if (!TextUtils.isEmpty(deviceName))
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_device);
        holder.deviceAddress.setText(device.getAddress());
        final int rssiPercent = (int) (100.0f * (127.0f + device.getRssi()) / (127.0f + 20.0f));
        holder.rssi.setImageLevel(rssiPercent);
    }

    @Override
    public long getItemId(final int position) {
        return devices.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return devices != null ? devices.size() : 0;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.device_address)
        TextView deviceAddress;
        @BindView(R.id.device_name) TextView deviceName;
        @BindView(R.id.rssi)
        ImageView rssi;

        private ViewHolder(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.findViewById(R.id.device_container).setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(devices.get(getAdapterPosition()));
                }
            });
        }
    }
}