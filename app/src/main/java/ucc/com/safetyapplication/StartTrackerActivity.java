package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionApi;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class StartTrackerActivity extends AppCompatActivity {
    // GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String userId, name; //name functionality not implemented yet
    AlertDialog.Builder alertDialog;
    Firebase workingRef;
    LocationRequest mLocationRequest; //might be wrong type
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    boolean mRequestingLocationUpdates;
    String mLastUpdateTime;
    TextView t;
    boolean isActivated;
    List<Address> addressList;
    public final static String SLIDE_STATUS = "com.realtimeprojects.uccmobapp.STATUS";

    Context context;

    boolean tracking;

    TextView lblLocation;
    TextView lblDuration;
    TextView lblStatus;
    ImageButton trackButton;
    TextView lblSlideLeft;

    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    TimerTask checkTimeFunction, checkWorkingFunction;
    Timer clockTimer;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        clockTimer = new Timer();
        checkTimeFunction = new checkTimer();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        lblLocation = (TextView) findViewById(R.id.txtAvailable);
        lblDuration = (TextView) findViewById(R.id.txtDuration);
        lblStatus = (TextView) findViewById(R.id.txtStatus);
        lblSlideLeft = (TextView)findViewById(R.id.lblSlideForHelp);
        trackButton = (ImageButton) findViewById(R.id.btnTrack);

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!isActivated) {
                startTracking();
            } else {
                stopTracking();
            }
            }
        });

        makeDialog();

        userId = getIntent().getStringExtra("userId");
        workingRef = new Firebase("https://ucc.firebaseio.com/users/" + userId + "/working");

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        manageLocationUpdates(locationManager);

        //need to ensure this only happens when coming from the emergency page
        //overridePendingTransition(R.anim.fadeout, R.anim.fadein); //change to slide_in and out

        checkWorking();
    }

    public void manageLocationUpdates(LocationManager locationManager) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isActivated) {
                    updateLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    lblLocation.setText(String.valueOf(location.getLatitude()));
                    getAddress(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                lblLocation.setText("Unavailable");
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                return true;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    Toast.makeText(context, "Emergency Mode Activated", Toast.LENGTH_SHORT).show();
                    emergencyMode();
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void makeDialog() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Reminder");
        alertDialog.setMessage("Hey there!\nAre you working today?");
        alertDialog.setPositiveButton("Yes", dialogClickListener);
        alertDialog.setNegativeButton("No", dialogClickListener);

        alertDialog.setCancelable(false);
        //might be another method required here when touching outside the dialog box
    }

    //CheckWorking(); //needs to run every minute

    public void startTracking() {
        trackButton.setBackgroundResource(R.drawable.go_offline_button);
        lblLocation.setText("Finding location...");
        lblStatus.setText("Working");
        tracking = true;
        isActivated = true;
        clockTimer.schedule(checkTimeFunction, 1000);
    }

    public void stopTracking() {
        trackButton.setBackgroundResource(R.drawable.start_button);
        lblLocation.setText("Unavailable");
        lblDuration.setText("00:00:00");
        lblStatus.setText("Unspecified");
        tracking = false;
        isActivated = false;
        checkTimeFunction.cancel();
        clockTimer.cancel();
    }

    public void emergencyMode() {
        Intent intent = new Intent(this, EmergencyModule.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout); //change to slide_in and out
    }

    public void updateLocation(String lat, String lng) {
        final Firebase locationRef = new Firebase("https://ucc.firebaseio.com/coordinates/" + userId + "/");

        locationRef.child("lat").setValue(lat);
        locationRef.child("lng").setValue(lng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.chat_icon) {
            Intent messaging = new Intent(this, Messages.class);
            startActivity(messaging);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    workingRef.setValue("Yes");
                    isActivated = true;

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    workingRef.setValue("No");
                    isActivated = false;
                    break;
            }
        }
    };

    public void checkWorking() {
        Calendar calendar = Calendar.getInstance();
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY); //you actually have to test this at some point, reset it once their done.

        ValueEventListener valueEventListener = workingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object workingStatus = dataSnapshot.getValue();

                if (workingStatus == "") {
                    if (currentHour >= 8) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getAddress(String lat, String lng) {
        Geocoder geoCoder = new Geocoder(StartTrackerActivity.this, Locale.getDefault());

        try {

            addressList = geoCoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 2);
            if ((addressList != null) && (addressList.size() > 0)) {
                Address address = addressList.get(0);
                lblLocation.setText(address.getLocality());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StartTracker Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ucc.com.safetyapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StartTracker Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ucc.com.safetyapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

class checkTimer extends TimerTask {
    int seconds, minutes, hours;

    @Override
    public void run() {
        seconds++;

        if (seconds == 60) {
            seconds = 0;
            minutes++;
        }

        if (minutes == 60) {
            minutes = 0;
            hours++;
        }

        if (hours == 24) {
            //stop counting

        }
    }
}