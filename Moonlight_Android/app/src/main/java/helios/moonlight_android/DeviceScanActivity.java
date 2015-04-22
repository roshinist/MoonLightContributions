package helios.moonlight_android;

import android.app.Activity;
import android.app.ListActivity; // Displays a list of items, and exposes event handlers
import android.bluetooth.BluetoothAdapter; // Allows to perform fundamental Bluetooth tasks
import android.bluetooth.BluetoothDevice; // Create a connection with respective device
import android.bluetooth.BluetoothManager; // Conduct overall Bluetooth Management
import android.content.Context; // Allows access to application-specific resources and classes
import android.content.Intent; // Launching of activities
import android.content.pm.PackageManager;
import android.os.Bundle; // A mapping from String values to various Parcelable types
import android.os.Handler; // Allows to send and process 'message' and Runnable objects
import android.view.LayoutInflater; // Instantiates a layout XML file into its corresponding view
import android.view.Menu;
import android.view.MenuItem;
import android.view.View; // Responsible for drawing and event handling
import android.view.ViewGroup; // Views for children of parent nodes
import android.widget.BaseAdapter; // Base class of common implementation for an adapter
import android.widget.ListView;
import android.widget.TextView; // Displays text to he user and optionally allows for editing
import android.widget.Toast;

import java.util.ArrayList; // Resizeable-array implementation of the 'List' interface

/**
 * Activity for scanning and displaying available BLE devices
 */
public class DeviceScanActivity extends ListActivity
{
    private BluetoothAdapter btAdapter;
    private boolean scanning;
    private LeDeviceListAdapter deviceListAdapter;
    private Handler handler;

    // Stops scanning after 60 seconds
    private static final long SCAN_PERIOD = 60000;

    // Initialize 'REQUEST_ENABLE_BT'
    private final static int REQUEST_ENABLE_BT = 1;

    // Setup BLE connection
    public void onCreate(Bundle savedInstanceState)
    {
        handler = new Handler();

        // Initialize Bluetooth adapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();

        // Check if Bluetooth is supported on the device
        if(btAdapter == null || !btAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    };

    // Scan for BLE compatible device
    private void scanLeDevice(final boolean enable)
    {
        if(enable) {
            // Stops scanning after a pre-defined scan period
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    scanning = false;
                    btAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
        } else {
            // End scan
            scanning = false;
            btAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();
    };

    // Deliver BLE scan results
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        deviceListAdapter.addDevice(device);
                        deviceListAdapter.notifyDataSetChanged();
                    }
                }
            );
        }
    };


    // Adapter for holding devices found through scanning
    private class LeDeviceListAdapter extends BaseAdapter
    {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter()
        {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        };

        public void addDevice(BluetoothDevice device)
        {
            if(!mLeDevices.contains(device))
            {
                mLeDevices.add(device);
            }
        };

        public BluetoothDevice getDevice(int position)
        {
            return mLeDevices.get(position);
        };

        public void clear()
        {
            mLeDevices.clear();
        };

        @Override
        public int getCount()
        {
            return mLeDevices.size();
        };

        @Override
        public Object getItem(int i)
        {
            return mLeDevices.get(i);
        };

        @Override
        public long getItemId(int i)
        {
            return i;
        };

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            /*
            ViewHolder viewHolder;
            // General ListView optimization code
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();

            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }

            viewHolder.deviceAddress.setText(device.getAddress());
            */

            return view;
        };
    }
}
