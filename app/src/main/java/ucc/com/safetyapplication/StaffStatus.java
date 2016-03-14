package ucc.com.safetyapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
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

public class StaffStatus extends AppCompatActivity {

    public List<Worker> workers;
    public String managerId;
    public ListView lvWorkers;
    public TextView txtWorkerCount;
    public String workerCount;

    public int numberOfWorkers, numberOfActiveWorkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        managerId = "1f0b0b84-538d-4134-91ad-6282f1f5afa3";
        workers = new ArrayList<Worker>();


        txtWorkerCount = (TextView)findViewById(R.id.txtWorkerCount);

        lvWorkers = (ListView)findViewById(R.id.lvWorkersToManage);
        lvWorkers.setAdapter(new WorkerAdapter(StaffStatus.this, workers, true));

        getWorkers();

        getWorkerCount();
        getActiveWorkers();

        txtWorkerCount.setText(String.valueOf(numberOfActiveWorkers) + "/" + String.valueOf(numberOfWorkers));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_staff_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //startActivity(new Intent(StaffStatus.this, ManagerHome.class));
                break;
            case R.id.search:
                // Get the intent, verify the action and get the query
                Intent intent = getIntent();
                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    //Do the search query e.g. doMySearch(query);
                }
                break;
        }

        return true;
    }

    public void getWorkerCount() {
        Firebase ref = new Firebase("https://ucc.firebaseio.com/pairs/" + managerId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfWorkers = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getActiveWorkers() {
        Firebase ref = new Firebase("https://ucc.firebaseio.com/pairs/" + managerId);

        Query queryRef = ref.equalTo("Working");

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfActiveWorkers = (int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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
                                workers.add(new Worker(s.child("name").getValue().toString(), s.child("phNumber").getValue().toString(),
                                    s.child("employeeId").getValue().toString(), (boolean)s.child("inDanger").getValue()));

                            }

                            ((BaseAdapter) ((ListView)findViewById(R.id.lvWorkers)).getAdapter()).notifyDataSetChanged();
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
}