package ucc.com.safetyapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ManagerHome extends AppCompatActivity implements View.OnClickListener {

    Button staffStatus, staffManagement, sendSafetyAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        staffStatus = (Button)findViewById(R.id.btnOne);
        staffManagement = (Button)findViewById(R.id.btnTwo);
        sendSafetyAlert = (Button)findViewById(R.id.btnThree);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOne:

                break;
            case R.id.btnTwo:
                //Some piece of functionality
                break;
            case R.id.btnThree:
                startActivity(new Intent(this, SafetyAlert.class));
                break;
        }
    }
}
