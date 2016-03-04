package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;

public class StartTrackerActivity extends AppCompatActivity {

    AlertDialog.Builder alertDialog, errorDialog;
    Firebase workingRef;
    Context context;

    boolean isActivated;
    String userId;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    TextView slideForHelp;
    ImageButton trackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        makeReminderDialog();

        userId = getIntent().getStringExtra("userId");
        workingRef = new Firebase("https://ucc.firebaseio.com/users/" + userId + "/working/");

        checkWorking();
    }

    @Override
    public void onStart() {
        super.onStart();

        trackButton = (ImageButton) findViewById(R.id.btnTrack);
        slideForHelp = (TextView) findViewById(R.id.lblSlideForHelp);

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackingFunctionality();
            }
        });
        slideForHelp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return slideFunctionality(v, event);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void trackingFunctionality() {
        if (!isActivated) {
            trackButton.setBackgroundResource(R.drawable.go_offline_button);
            isActivated = true;
        } else {
            trackButton.setBackgroundResource(R.drawable.start_button);
            isActivated = false;
        }
    }

    public boolean slideFunctionality(View v, MotionEvent event) {
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
                return false;
        }
    }

    public void makeReminderDialog() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Reminder");
        alertDialog.setMessage("Hey there!\nAre you working today?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                workingRef.setValue("Yes");
                isActivated = true;
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                workingRef.setValue("No");
                isActivated = false;
            }
        });
        alertDialog.setCancelable(false);
    }

    public void makeErrorDialog(FirebaseError firebaseError) {
        errorDialog = new AlertDialog.Builder(this);
        errorDialog.setTitle("Error");
        errorDialog.setMessage(firebaseError.getMessage());
        errorDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        errorDialog.setCancelable(false);
    }

    public void emergencyMode() {
        Intent intent = new Intent(this, EmergencyModule.class);
        startActivity(intent);
    }

    public void checkWorking() {
        Calendar calendar = Calendar.getInstance();
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        ValueEventListener valueEventListener = workingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String workingStatus = dataSnapshot.getValue().toString();

                if (workingStatus == "") {
                    if (currentHour >= 8) {
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                makeErrorDialog(firebaseError);
            }
        });
    }
}