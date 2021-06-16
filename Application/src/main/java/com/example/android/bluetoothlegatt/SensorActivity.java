package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.ViewPager;

import com.example.android.bluetoothlegatt.adapter.DiscoveredBluetoothDevice;
import com.example.android.bluetoothlegatt.fragments.ChartFragment;
import com.example.android.bluetoothlegatt.fragments.DeviceFragment;
import com.example.android.bluetoothlegatt.viewmodels.SensorViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SensorActivity extends AppCompatActivity {

    public static final String EXTRA_DEVICE  = "com.example.android.bluetoothlegatt.EXTRA_DEVICE";
    static final int NUM_ITEMS = 2;

    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    private int[] tabIcons = {R.drawable.ic_sensor_info, R.drawable.ic_show_chart};

    private SensorViewModel sensorVM;

    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();

        final MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(deviceName != null ? deviceName : getString(R.string.unknown_device));
        toolbar.setSubtitle(deviceAddress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupTabPage();

        // configure the view model
        sensorVM = new ViewModelProvider(this).get(SensorViewModel.class);
        sensorVM.connect(device);

        // setupView;
        sensorVM.getConnectionState().observe(this,value ->{
            if(value){
                // display the connected status on the toolbar
            }else{
                //display disconnected on toolbar
            }
        });

        sensorVM.getSensorValue().observe(this, value ->{
            //TODO
        });
    }

    public SensorViewModel getSensorVM(){
        return sensorVM;
    }

    private void setupTabPage(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DeviceFragment(), "Sensor");
        adapter.addFrag(new ChartFragment(), "Chart");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
