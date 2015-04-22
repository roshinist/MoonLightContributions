package helios.moonlight_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import static android.widget.Toast.LENGTH_LONG;

/**
 * Getting Location Updates.
 *
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 *
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class MapsActivity extends FragmentActivity {
    public static final String TAG = MapsActivity.class.getSimpleName();

    private Button mShowGPSLocationButton;
    private Button mShowAddressButton;

    private TextView mAddressTextView;

    private AppLocationService appLocationService;

    private GoogleMap mMap;

    LatLng i;
    MarkerOptions markerOptions;

    List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();

        mAddressTextView = (TextView) findViewById(R.id.tvAddress);
        appLocationService = new AppLocationService(
                MapsActivity.this);

        mShowGPSLocationButton = (Button) findViewById(R.id.btnGPSShowLocation);
        mShowGPSLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    double latitude = gpsLocation.getLatitude();
                    double longitude = gpsLocation.getLongitude();
                    String result = "Latitude: " + gpsLocation.getLatitude() +
                            "\nLongitude: " + gpsLocation.getLongitude();
                    Toast.makeText(MapsActivity.this, result, LENGTH_LONG).show();
                    //mAddressTextView.setText(result);

                } else {
                    showSettingsAlert();
                }
            }
        });

        mShowAddressButton = (Button) findViewById(R.id.btnShowAddress);
        mShowAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Location location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    final double currentLatitude = location.getLatitude();
                    final double currentLongitude = location.getLongitude();
                    final LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(currentLatitude, currentLongitude,
                            getApplicationContext(), new GeocoderHandler());
                    /////////////////////////////////////
                    //Update UI:
                    LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("You are Here!"))
                            .showInfoWindow();


                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (marker.getTitle().equals("You are Here!")) {
                                locationAddress.getAddressFromLocation(currentLatitude, currentLongitude,
                                        getApplicationContext(), new GeocoderHandler());
                            }
                            return true;
                        }
                    });
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    ////////////////////////////////////

                    ///////////////////////////////////////////////////////////////////
                    /*Use currentLatitude & currentLongitude for Bike Index.
                        Extract lat and long from database
                        Display Markers
                    */
                    Vector<LatLng> v = new Vector<>();
                    v.add(new LatLng(37.331996, -120.495469));
                    v.add(new LatLng(37.307610, -120.477389));

                    for (int i = 0; i < v.size(); i++) {
                        final double lat = v.get(i).latitude;
                        final double lon = v.get(i).longitude;
                        final String latText = lat + "";
                        final String longText = lon + "";
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(v.get(i))
                                        .title("Name of Bike: " + latText + " , " + longText)
                                        .snippet(".....")
                        );
                        markers.add(marker);
                    }
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            //marker.showInfoWindow();
                            locationAddress.getAddressFromLocation(marker.getPosition().latitude, marker.getPosition().longitude,
                                    getApplicationContext(), new GeocoderHandler());
                            marker.setSnippet(String.valueOf(mAddressTextView.getText()));
                            Log.i(TAG, String.valueOf(mAddressTextView.getText()));
                            marker.showInfoWindow();

                            // Adding Marker on the touched location with address
                            //new ReverseGeocodingTask(getBaseContext()).execute(marker.getPosition());
                            return false;
                        }
                    });
//                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                        @Override
//                        public void onMapClick(LatLng latLng) {
//                            i = latLng;
//                            mMap.clear();
//                            mMap.animateCamera(CameraUpdateFactory.newLatLng(i));
//
//                            // Creating a marker
//                            markerOptions = new MarkerOptions();
//
//                            // Setting the position for the marker
//                            markerOptions.position(i);
//
//                            // Placing a marker on the touched position
//                            mMap.addMarker(markerOptions);
//
//                            // Adding Marker on the touched location with address
//                            new ReverseGeocodingTask(getBaseContext()).execute(i);
//                        }
//                    });
//                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            if (marker.getPosition()) {
//                                locationAddress.getAddressFromLocation(lat, lon,
//                                        getApplicationContext(), new GeocoderHandler());
//                            }
//                            return true;
//                        }
//                    });

                    ///////////////////////////////////////////////////////////////////
                } else {
                    showSettingsAlert();
                }

            }

        });

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MapsActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MapsActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            mAddressTextView.setText(locationAddress);
        }
    }
}
