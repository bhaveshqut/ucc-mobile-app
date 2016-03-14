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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffManagementActivity extends AppCompatActivity {

    ListView workersListView;
    List<Worker> workersList;
    Button addWorkerButton;
    List<String> chosenPositions;
    String managerId;
    AlertDialog.Builder alertDialog;
    String hashString;

    FloatingActionButton addWorkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWorkers();

        addWorkers = (FloatingActionButton)findViewById(R.id.addWorkers);

        managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";
        alertDialog = setupErrorDialog();

        workersListView = (ListView)findViewById(R.id.lvWorkersToManage);
        workersList = new ArrayList<>();
        workersListView.setAdapter(new WorkerAdapter(StaffManagementActivity.this, workersList, false));

        addWorkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StaffManagementActivity.this, StaffLookup.class));
            }
        });

        /*addWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWorkers();
            }
        });*/

        workersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                chosenPositions = ((WorkerAdapter)workersListView.getAdapter()).getChosenPositions();


                final TextView employeeId = (TextView)view.findViewById(R.id.workerId);

                        if (!chosenPositions.contains(position)) {
                            chosenPositions.add(employeeId.getText().toString());
                        } else {
                            for (int i = 0; i < chosenPositions.size(); i++) {
                                if (chosenPositions.get(i).equals(employeeId.getText())) {
                                    chosenPositions.remove(i);
                                }
                            }
                        }
            }
        });
    }

    public void getWorkers() {
        final Firebase workersRef = new Firebase("https://ucc.firebaseio.com/");


        workersRef.child("users/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                for (final DataSnapshot s : dataSnapshot.getChildren()) {



                    workersRef.child("pairs/" + s.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {

                            if (s.child("name").exists() && s.child("phNumber").exists() && s.child("employeeId").exists()
                                    && s.child("inDanger").exists()) {
                                workersList.add(new Worker(s.child("name").getValue().toString(), s.child("phNumber").getValue().toString(),
                                        s.child("employeeId").getValue().toString(), (boolean)s.child("inDanger").getValue()));

                            }

                            ((BaseAdapter) ((ListView)findViewById(R.id.lvWorkersToManage)).getAdapter()).notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            int i = 0;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                int ij = 11;
            }
        });
    }

    public AlertDialog.Builder setupErrorDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(StaffManagementActivity.this);
        alert.setTitle("Error");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //does nothing at this stage
            }
        });

        return alert;
    }

    public void addWorkers() {
        Firebase fire = new Firebase("https://ucc.firebaseio.com/pairs/" + managerId + "/");

        for (int i = 0; i < chosenPositions.size(); i++) {
            addWorker(fire, i);
        }
    }

    public String getHashId(String employeeId) {
        Firebase fire = new Firebase("https://ucc.firebaseio.com/users/");

        Query q = fire.orderByChild("employeeID").equalTo(employeeId);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hashString = String.valueOf(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Not sure yet
            }
        });

        return hashString;

        //there has to be a better way...
    }

    public void addWorker(final Firebase firebase, int i) {
        final int position = i;

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> pairMap = new HashMap<String, String>();
                pairMap.put(getHashId(chosenPositions.get(position)), "");
                firebase.setValue(pairMap);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                alertDialog.setMessage(firebaseError.getMessage());
                alertDialog.show();
            }
        });
    }
}
