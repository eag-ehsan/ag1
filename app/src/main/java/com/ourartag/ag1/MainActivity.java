package com.ourartag.ag1;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.graphics.BitmapFactory;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.neshan.mapsdk.MapView;
import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.model.Marker;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    private MapView map;
    private AnimationStyle animSt;
    LatLng latlng;
    private Marker marker;
    TextView latitudeTextView, longitTextView;
    TextView adminCommands;
    int iCounter;
    Button btn,btn2;
    WebView mywv;
    Javascript myjavascript;
    esTimer timer1000;
    esTimer myMainTimer;
    int PERMISSION_ID = 44;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iCounter = 0;

        adminCommands = findViewById(R.id.adminc);
        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);

        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        mywv = findViewById(R.id.webview1);
        WebSettings webSettings = mywv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myjavascript = new Javascript(getApplicationContext());
        mywv.addJavascriptInterface(myjavascript, "Android");
        map = findViewById(R.id.map);
        map.setTrafficEnabled(true);

        map.moveCamera(new LatLng(35.767234, 51.330743), 0);
        map.setZoom(14, 0);
        map.getSettings().setZoomControlsEnabled(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        timer1000 = new esTimer(new Runnable() {public void run() {getAndSendCoords();}}, 30000, false);
        myMainTimer = new esTimer(new Runnable() {public void run() {mainLoop();}}, 5000, false);

        // method to get the location
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getLastLocation();
                map.setZoom(14, 0);
                Float temfll = Float.valueOf(latitudeTextView.getText().toString());
                Float tempfln = Float.valueOf(longitTextView.getText().toString());
//                map.getSettings().setZoomControlsEnabled(true);
                addUserMarker(new LatLng(temfll, tempfln));

//                map.addMarker(createMarker(latlng));
                map.moveCamera(new LatLng(temfll, tempfln), .5f);

            }
        });
        //getLastLocation();


    }
    private void addUserMarker(LatLng loc) {
        //remove existing marker from map
        if (marker != null) {
            marker.setLatLng(loc);
        } else {
            // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
            // and then call buildStyle method on it. This method returns an object of type MarkerStyle
            MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
            markStCr.setSize(30f);
            markStCr.setAnchorPointY(0);
            markStCr.setAnchorPointX(0);
            markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.markerg)));
            MarkerStyle markSt = markStCr.buildStyle();

            // Creating user marker
            marker = new Marker(loc, markSt);

            // Adding user marker to map!
            map.addMarker(marker);

        }

    }
    private void runAdmincmd(String cmmd) {
        if(cmmd.equals("run1")){
            getAndSendCoords();
        }else if(cmmd.equals("run2")){
            //timer1000.startTimer();

        }else if(cmmd.equals("run3")){

        }else if(cmmd.equals("run4")){

        }else if(cmmd.equals("run5")){

        }
    }

    private void getAndSendCoords() {
        getLastLocation();
        String strtemp = "";
        strtemp = "https://www.ourartag.com/posi.php?l=" + latitudeTextView.getText() + "&i=1&n=" + longitTextView.getText();
        mywv.loadUrl(strtemp);

    }


    private void mainLoop() {

        if(myjavascript.isThereAnyData()) {
            String strtmp = "New Admin cmd: " + myjavascript.getMyData();
            adminCommands.setText(strtmp);
        }


    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };


    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}