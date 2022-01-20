package com.example.tidbits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tidbits.firebase.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    Button loginButton;
    TextView register_account;
    EditText email_add, password;
    private FirebaseAuth auth;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_add = findViewById(R.id.email);
        password = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_add.getText().toString();
                String pass = password.getText().toString();
                if (email.isEmpty() && pass.isEmpty()) {
                    CharSequence text = getString(R.string.no_email_pass_error);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    return;
                }
                if (email.isEmpty()) {
                    CharSequence text = getString(R.string.no_email_error);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    return;
                }
                if (pass.isEmpty()) {
                    CharSequence text = getString(R.string.no_pass_error);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    return;
                }
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            CharSequence text = getString(R.string.logged_in);
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                            toast.show();
                            Intent intent = new Intent(LoginActivity.this, UserSwitcherActivity.class);
                            intent.putExtra("uid", task.getResult().getUser().getUid());
                            startActivity(intent);
                        } else {
                            CharSequence text = getString(R.string.login_error);
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                            toast.show();
                            return;
                        }
                    }
                });

            }
        });

        register_account = (TextView) findViewById(R.id.registerNow);
        register_account.setOnClickListener(new View.OnClickListener() {
            /**
             * Facebook onClick
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}