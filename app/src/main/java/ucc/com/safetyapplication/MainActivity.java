package ucc.com.safetyapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    int loginAttempts;
    EditText emailTxt, passwordTxt;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginAttempts = 0;
        emailTxt = (EditText)findViewById(R.id.txtEmail);
        passwordTxt = (EditText)findViewById(R.id.txtPassword);
        //code = (EditText)findViewById(R.id.txtCode);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:

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
    }*/

    public void loginUser() {
        final String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        final Firebase root = new Firebase("https://ucc.firebaseio.com/");

        root.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                String uID = authData.getUid();
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                Firebase users = new Firebase("https://ucc.firebaseio.com/users/" + uID + "/permission/");

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String permission = dataSnapshot.getValue().toString();

                        switch (permission) {
                            case "M":
                                System.out.println("Manager logging in");
                                break;
                            case "E":
                                System.out.println("Employee logging in");
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        //Not needed yet.
                    }
                });
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                if (loginAttempts < 5) {
                    loginAttempts++;
                    System.out.println("Failed login, try again...You have " + (5-loginAttempts+1) + "attempts before you are locked out");
                } else {
                    System.out.println("Exceeded amount of login attempts!");
                }
            }
        });
    }
}
