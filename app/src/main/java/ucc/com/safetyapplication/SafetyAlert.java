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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafetyAlert extends AppCompatActivity {

    String managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";

    AlertDialog.Builder textAlert, errorAlert;
    Context context;
    LayoutInflater li;
    View promptsView;

    ListView messageList;
    EditText input;
    Button sendAlert, cancelSend, sendText;

    List<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = SafetyAlert.this;

        li = LayoutInflater.from(context);
        promptsView = li.inflate(R.layout.safety_alert_dialog, null);

        messages = new ArrayList<String>();

        messageList = (ListView)findViewById(R.id.lvMessages);
        input = (EditText)findViewById(R.id.txtMessage);
        sendText = (Button)findViewById(R.id.btnSend);

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAlert();
            }
        });

        textAlert = new AlertDialog.Builder(context);
        textAlert.setTitle("New Alert");
        textAlert.setView(promptsView);

        //I had to break the rules here, make a negative action a positive one.

        textAlert.setNegativeButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendAlert();
                addAlertToList();
                dialog.cancel();
            }
        });

        textAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        messageList.setAdapter(new MessagesAdapter(context, messages));
    }

    public void makeErrorDialog(FirebaseError firebaseError) {
        errorAlert = new AlertDialog.Builder(this);
        errorAlert.setTitle("Error");
        errorAlert.setMessage(firebaseError.getMessage());

        errorAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        errorAlert.setCancelable(false);
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
        }

        return true;
    }


    public void addAlertToList() {

    }

    public void sendAlert() {
        final Firebase alertRef = new Firebase("https://ucc.firebaseio.com/alerts/" + managerId + "/");

        alertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(String.valueOf((int) System.currentTimeMillis()), input.getText().toString());
                alertRef.push().setValue(map);
                messages.add(input.getText().toString());
                ((BaseAdapter)messageList.getAdapter()).notifyDataSetChanged();
                input.setText("");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                makeErrorDialog(firebaseError);
            }
        });
    }
}
