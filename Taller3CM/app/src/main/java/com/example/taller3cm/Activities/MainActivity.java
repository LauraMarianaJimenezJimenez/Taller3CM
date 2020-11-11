package com.example.taller3cm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taller3cm.Other.NotificationService;
import com.example.taller3cm.R;
import com.example.taller3cm.Other.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "Taller Autenticación";
    EditText edtUser, edtPassword;
    Button btnLogin, btnSignup;
    private FirebaseAuth mAuth;
    public static final String CHANNEL_ID = "M&L App";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUser = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignin);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        createNotificationChannel();
        Intent i = new Intent(MainActivity.this, NotificationService.class);
        NotificationService.enqueueWork(MainActivity.this, i);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
}

    private void loginUser() {
        String email = edtUser.getText().toString();
        String password = edtPassword.getText().toString();

        boolean validEmail = Utils.validateEmail(email);
        boolean validPass = Utils.validatePassword(password);

        if (!validEmail) {
            if (email.isEmpty()) {
                edtUser.setError("Requerido");
            } else {
                edtUser.setError("E-mail no válido");
            }
        } else edtUser.setError(null);
        if (!validPass) {
            if (password.isEmpty()) {
                edtPassword.setError("Requerido");
            } else {
                edtPassword.setError("6 caracteres mínimos");
            }
        } else edtPassword.setError(null);

        if (validEmail && validPass) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null) {
            Intent intent = new Intent(getBaseContext(), MapActivity.class);
            startActivity(intent);
        }
        else{
            edtUser.setText("");
            edtPassword.setText("");
        }
    }

    private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }}
