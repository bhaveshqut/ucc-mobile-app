package ucc.com.safetyapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.net.URI;

public class EmergencyModule extends AppCompatActivity implements View.OnClickListener {
    Button btn2, btn3, btn4;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    final String managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_module);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);
        btn4 = (Button)findViewById(R.id.button4);

        btn2.setOnClickListener(this);

        //overridePendingTransition(R.anim.fadein, R.anim.fadeout); //change to slide_in and out
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

    public void callManager(){
        Uri number = Uri.parse("tel:0416910468");                       //TEST CASE PHONE NUMBER, MANAGER PHONE NUMBER SHOULD BE EXTRACTED FROM DATABASE
        Intent callIntent = new Intent(Intent.ACTION_CALL,number);      //IMPLICIT INTENT TO USE CALL APPLICATION
        startActivity(callIntent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > -(MIN_DISTANCE)) {
                    Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show();
                    trackerPage();
                } else {
                    // consider as something else - a screen tap for example
                }
                break;

            case MotionEvent.ACTION_UP:
                x1 = event.getX();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void callEmergency(){
        Uri number = Uri.parse("tel:0416910468");
        Intent callIntent = new Intent(Intent.ACTION_DIAL,number);
        startActivity(callIntent);
    }

    public void trackerPage() {
        Intent intent = new Intent(this, StartTrackerActivity.class);
        startActivity(intent);
    }

    public void sendLocation() {
        Firebase viewableRef = new Firebase("https://ucc.firebaseio.com/users/" + managerId + "/");
        if (viewableRef.child("emergency").equals(true)) {
            viewableRef.child("emergency").setValue(false);
        } else {
            viewableRef.child("emergency").setValue(true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                callManager();
                break;
            case R.id.button3:
                callEmergency();
                break;
            case R.id.button4:
                sendLocation();
                break;
        }
    }
}

