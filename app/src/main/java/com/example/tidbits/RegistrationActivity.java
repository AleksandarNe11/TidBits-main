package com.example.tidbits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    Button register;
    TextView login;
    EditText email_add, password, confirm_password;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email_add = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirmPassword);
        auth = FirebaseAuth.getInstance();
        register = (Button) findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_add.getText().toString();
                String pass = password.getText().toString();
                String confirm_pass = confirm_password.getText().toString();
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
                if (confirm_pass.isEmpty()) {
                    CharSequence text = getString(R.string.no_con_pass_error);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    return;
                }
                if (pass.equals(confirm_pass)) {
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                CharSequence text = getString(R.string.registered);
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                                // create user in Realtime DB users
                                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
                                HashMap<String, Integer> userHash = new HashMap<>();
                                userHash.put("numChilds", 0);
                                userReference.setValue(userHash);
                                finish();
                            } else {
                                CharSequence text = getString(R.string.registration_error);
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                            }
                        }
                    });
                } else {
                    CharSequence text = getString(R.string.pass_mismatch);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    return;
                }
            }
        });
        login = (TextView) findViewById(R.id.loginNow);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}