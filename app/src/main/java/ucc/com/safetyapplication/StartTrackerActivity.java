package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.Calendar;
import java.util.Date;

public class StartTrackerActivity extends AppCompatActivity {

    String userId, name; //name functionality not implemented yet
    AlertDialog.Builder alertDialog;
    Firebase workingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        userId = getIntent().getStringExtra("userId");
        //name = getIntent().getStringExtra("name");
        workingRef = new Firebase("https://ucc.firebaseio.com/users/" + userId + "/working");

        CheckWorking(); //needs to run every minute

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Reminder");
        alertDialog.setMessage("Hey there!\nAre you working today?");
        alertDialog.setPositiveButton("Yes", dialogClickListener);
        alertDialog.setNegativeButton("No", dialogClickListener);
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
                    //you can track, Google Maps bruh...
                    //location will probably be moved to a separate hierarchy
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    workingRef.setValue("No");
                    break;
            }
        }
    };

    public void CheckWorking() {
        Calendar calendar = Calendar.getInstance();
        final int currentHour = calendar.get(Calendar.HOUR); //you actually have to test this at some point, reset it once their done.

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

    public void startTracking(View view){
        String location_value = "Ann Street"; //CURRENT VALUES ARE TEST CASE VALUES. VALUES TO BE RETRIEVED FROM DATABASE IN REAL TIME
        String duration_value = "00:00";
        String status_value = "At work";
        String button_value = "Go offline";

        TextView updated_location = (TextView) findViewById(R.id.textView);
        TextView updated_duration = (TextView) findViewById(R.id.textView4 );
        TextView updated_status = (TextView) findViewById(R.id.textView6);
        Button updated_button = (Button)findViewById(R.id.button);

        updated_location.setText(location_value);
        updated_duration.setText(duration_value);
        updated_status.setText(status_value);
        updated_button.setText(button_value);
    }
}