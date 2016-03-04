package ucc.com.safetyapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    int loginAttempts;
    EditText emailTxt, passwordTxt;
    Button btnLogin;
    Intent managerHome, workerHome;
    String userId, name;
    AlertDialog.Builder loginAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        managerHome = new Intent(this, SafetyAlert.class);
        workerHome = new Intent(this, ManagerHome.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loginAttempts = 0;
        emailTxt = (EditText)findViewById(R.id.txtEmail);
        passwordTxt = (EditText)findViewById(R.id.txtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    public void makeDialog() {
        loginAlert = new AlertDialog.Builder(this);
        loginAlert.setTitle("Error");
        loginAlert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        loginAlert.setCancelable(false);
    }

    public void setLoginFailed(FirebaseError firebaseError) {
        loginAlert.setMessage(firebaseError.getMessage());
        loginAlert.show();

        //If you want to number the login attempts

        /*if (loginAttempts < 5) {
            loginAlert.show();
            loginAttempts++;
        } else {
            loginAlert.setMessage("Exceeded amount of login attempts, please contact system administration.");
            loginAlert.show();
        }*/
    }

    public void loginUser() {
        final String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        final Firebase root = new Firebase("https://ucc.firebaseio.com/");

        root.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(final AuthData authData) {
                String uID = authData.getUid();
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                Firebase users = new Firebase("https://ucc.firebaseio.com/users/" + uID + "/permission/");

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String permission = dataSnapshot.getValue().toString();

                        switch (permission) {
                            case "M":
                                startActivity(managerHome);
                                break;
                            case "E":
                                workerHome.putExtra("userId", authData.getUid());
                                startActivity(workerHome);
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        setLoginFailed(firebaseError);
                    }
                });
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                setLoginFailed(firebaseError);
            }
        });
    }
}
