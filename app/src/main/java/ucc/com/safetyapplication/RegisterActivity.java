package ucc.com.safetyapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText emailTxt, passwordTxt, code;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                GoHome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        emailTxt = (EditText)findViewById(R.id.txtEmail);
        passwordTxt = (EditText)findViewById(R.id.txtPassword);
        //code = (EditText)findViewById(R.id.txtCode);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();

            }
        });
    }

    public void registerUser() {
        final String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        //If no accounts directory exists, make one
        final Firebase root = new Firebase("https://ucc.firebaseio.com/");
        root.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                String userId = result.get("uid").toString();

                System.out.println("Successfully created user account with uid: " + userId);

                root.child("permissions").child("uid").setValue(userId);
                root.child("permissions").child("perms").setValue(email);

                GoHome();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                System.out.println("Error! " + firebaseError.getMessage());
            }
        });
    }

    public void GoHome() {
        Intent goHome = new Intent(this, MainActivity.class);
        startActivity(goHome);
    }
}
