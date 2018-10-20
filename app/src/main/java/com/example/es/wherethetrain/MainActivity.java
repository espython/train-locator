package com.example.es.wherethetrain;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.*;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    /* Vars Declaration */
    private FusedLocationProviderClient mFusedproviderClient;
    private static final int FINE_LOCATION_REQUEST_CODE = 101;
    RelativeLayout mainView;
    TextView latitude;
    TextView longitude;
    private static final String TAG = "MainActivity";
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean updatesOn = false;
    private Context appContext;

    /* Ending */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        /*  If the Android version is lower than Jellybean, use this call to hide
            the status bar. */
        /* Make Status Bar transparent */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            Objects.requireNonNull(getSupportActionBar()).hide();
        }


        RelativeLayout mainView = new RelativeLayout(this);
        RelativeLayout.LayoutParams mainViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainView.setLayoutParams(mainViewParams);
        TextView helloTextView = new TextView(appContext);
        RelativeLayout.LayoutParams helloViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        helloViewParams.alignWithParent = true;
        helloViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        helloTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        helloTextView.setLayoutParams(helloViewParams);

        RelativeLayout.LayoutParams latViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams longViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView latTextView = new TextView(appContext);

        latViewParams.alignWithParent =true;
        latViewParams.addRule(RelativeLayout.ALIGN_BOTTOM);
        latViewParams.bottomMargin = 96;
        latTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        latTextView.setLayoutParams(latViewParams);

        final TextView lonTextView = new TextView(appContext);
        longViewParams.alignWithParent = true;
        longViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        longViewParams.topMargin = 76;
        lonTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        lonTextView.setLayoutParams(longViewParams);

        helloTextView.setText("Hello from Java");
        mainView.addView(helloTextView);
        mainView.addView(latTextView);
        mainView.addView(lonTextView);
        mainView.setBackgroundColor(ActivityCompat.getColor(appContext,R.color.mainColor));


        mFusedproviderClient = LocationServices.getFusedLocationProviderClient(appContext);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(7500); //use a value of  about 10 to 15s for a real app
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (location != null){
                        Log.d(TAG,"Your lat = "+ location.getLatitude());
                        latTextView.setText(String.format(" your lat is %s", location.getLatitude()));
                        lonTextView.setText(String.format(" your lon is %s", location.getLongitude()));
                        Log.d(TAG,"Your long = "+ location.getLongitude());
                    }

                }
            }
        };

        setContentView(mainView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedproviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedproviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case (FINE_LOCATION_REQUEST_CODE):
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSION SUCCESSFULLY  GRANTED");
                } else {
                    Toast.makeText(this, "Please Enable  your location to get your accurate weather data ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
