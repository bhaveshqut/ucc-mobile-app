package ucc.com.safetyapplication;

import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.telephony.SmsManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Messages extends AppCompatActivity {

    String userId = "c9c03565-e99c-4318-91d8-cb44bca1bc64", managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";

    List<String> messages;

    Button btnSend;
    ListView lvMessages;
    TextView txtMessage;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSend = (Button) findViewById(R.id.btnSend);
        txtMessage = (TextView) findViewById(R.id.txtMessage);

        messages = new ArrayList<String>();
        lvMessages = (ListView)findViewById(R.id.lvMessages);

        lvMessages.setAdapter(new MessagesAdapter(getApplicationContext(), messages));
        lvMessages.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (txtMessage.getText() != "") {
                        Firebase phoneRef = new Firebase("https://ucc.firebaseio.com/users/" + managerId + "/ph/");
                        final String phNum;

                        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String text = txtMessage.getText().toString();
                                SmsManager.getDefault().sendTextMessage(snapshot.getValue().toString(), null,
                                        txtMessage.getText().toString(), null, null);
                                messages.add(text);
                                ((BaseAdapter) ((ListView)findViewById(R.id.lvMessages)).getAdapter()).notifyDataSetChanged();
                                //((BaseAdapter) lvMessages.getAdapter()).notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    }
            }
        });
    }

}