package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
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
    boolean isActivated, hasStarted = false;
    List<Address> addressList;

    Button messageButton;

    TextView updated_location;
    TextView updated_duration;
    TextView updated_status;
    Button updated_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        messageButton = (Button)findViewById(R.id.btnMessages);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Messages.class));
            }
        });

        updated_location = (TextView) findViewById(R.id.txtSuburb);
        updated_duration = (TextView) findViewById(R.id.textView4);
        updated_status = (TextView) findViewById(R.id.textView6);
        updated_button = (Button)findViewById(R.id.button);

        updated_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button) {
                    if (isActivated) {
                        isActivated = false;
                        updated_button.setText("Start");
                        updated_duration.setText("00:00");
                        updated_location.setText("Unavailable");
                        updated_status.setText("At home");
                    } else {
                        isActivated = true;
                        updated_button.setText("Turn Off");
                        updated_status.setText("Working");
                    }
                }
            }
        });

        //updateValuesFromBundle(savedInstanceState);
        t = (TextView)findViewById(R.id.textView4);
        userId = getIntent().getStringExtra("userId");
        //name = getIntent().getStringExtra("name");
        workingRef = new Firebase("https://ucc.firebaseio.com/users/" + userId + "/working");
        // Create a GoogleApiClient instance

        CheckWorking(); //needs to run every minute

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Reminder");
        alertDialog.setMessage("Hey there!\nAre you working today?");
        alertDialog.setPositiveButton("Yes", dialogClickListener);
        alertDialog.setNegativeButton("No", dialogClickListener);

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);


            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (isActivated) {
                        updateLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        t.setText(String.valueOf(location.getLatitude()));
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

                }
            });
    }

    public void updateLocation(String lat, String lng) {
        final Firebase locationRef = new Firebase("https://ucc.firebaseio.com/coordinates/" + userId + "/");

        //if your still want updates
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    workingRef.setValue("Yes");
                    isActivated = true;
                    updated_button.setText("Go Offline");
                    //you can track, Google Maps bruh...
                    //location will probably be moved to a separate hierarchy
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    workingRef.setValue("No");
                    isActivated = false;
                    updated_button.setText("Start");
                    break;
            }
        }
    };

    public void CheckWorking() {
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
                updated_location.setText(address.getLocality());
            }
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

   /* public void startTracking(View view){
        String location_value = "Ann Street"; //CURRENT VALUES ARE TEST CASE VALUES. VALUES TO BE RETRIEVED FROM DATABASE IN REAL TIME
        String duration_value = "00:00";
        String status_value = "At work";
        String button_value = "Start";

        if (!hasStarted) {

        } else {
            if (isActivated) {
                isActivated = false;
                updated_button.setText("Turn Off");
            } else {
                isActivated = true;
                updated_button.setText("Start");
            }
        }




    }*/
}