package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SafetyAlert extends AppCompatActivity {

    String managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";
    AlertDialog.Builder textAlert;
    Context context;
    LayoutInflater li;
    View promptsView;
    EditText input;
    Button sendAlert, cancelSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        li = LayoutInflater.from(context);
        promptsView = li.inflate(R.layout.safety_alert_dialog, null);

        input = (EditText)promptsView.findViewById(R.id.txtAlert);

        //I had to break the rules here, make a negative action a positive one.



        textAlert = new AlertDialog.Builder(this);
        textAlert.setTitle("New Alert");
        textAlert.setView(promptsView);

        textAlert.setNegativeButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendAlert();
            }
        });

        textAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Closes the dialog box
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_safety_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_icon) {
            textAlert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendAlert() {
        final Firebase alertRef = new Firebase("https://ucc.firebaseio.com/alerts/" + managerId + "/");

        Map<String, String> map = new HashMap<String, String>();
        map.put(String.valueOf((int) System.currentTimeMillis()), input.getText().toString());
        alertRef.push().setValue(map);
    }
}
