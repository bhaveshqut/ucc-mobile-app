package ucc.com.safetyapplication;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafetyAlert extends AppCompatActivity {

    String managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";

    AlertDialog.Builder textAlert, errorAlert;
    Context context;
    LayoutInflater li;
    View promptsView;
    String userType;

    ListView messageList;
    EditText input;
    Button sendAlert, cancelSend, sendText;
    LinearLayout enterAlert, messagesLayout;

    List<Message> messages = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        context = SafetyAlert.this;

        userType = "E";

        li = LayoutInflater.from(context);
        promptsView = li.inflate(R.layout.safety_alert_dialog, null);


        enterAlert = (LinearLayout)findViewById(R.id.enterAlert);
        messageList = (ListView)findViewById(R.id.lvMessages);
        input = (EditText)findViewById(R.id.txtMessage);
        sendText = (Button)findViewById(R.id.btnSend);
        messagesLayout = (LinearLayout)findViewById(R.id.messagesLayout);

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

    public void showMessages() {
        Firebase h = new Firebase("https://ucc.firebaseio.com/alerts/" + managerId + "/");

        h.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot smallShot : dataSnapshot.getChildren()) {
                    messages.add(smallShot.getValue(Message.class));
                }

                ((BaseAdapter) messageList.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (userType == "E") {
            ((ViewManager) enterAlert.getParent()).removeView(enterAlert);

            ViewGroup.LayoutParams params = messagesLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        }

        showMessages();

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
        //getMenuInflater().inflate(R.menu.menu_safety_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(context, ManagerHome.class));
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
                Map<String, String> map = new HashMap<>();
                map.put(String.valueOf((int) System.currentTimeMillis()), input.getText().toString());
                alertRef.push().setValue(map);
                messages.add(new Message(input.getText().toString(), String.valueOf(System.currentTimeMillis())));
                ((BaseAdapter)messageList.getAdapter()).notifyDataSetChanged();
                input.setText("");

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                makeErrorDialog(firebaseError);
            }
        });
    }
}
