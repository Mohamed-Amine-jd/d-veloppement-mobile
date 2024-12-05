package com.iset.tp7.Auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.tp7.databinding.ActivityLoginBinding;
import com.iset.tp7.CotrollerTeacher.HomeEnseg;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;

import com.iset.tp7.Activities.HomeStudent;
import com.iset.tp7.Activities.MainActivity;
import com.iset.tp7.Session.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper db;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        checkUserHaveNotificationPermission();

        binding.btnLogin.setOnClickListener(view -> loginUser());
        binding.btnGoToRegister.setOnClickListener(view -> navigateToRegister());
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.checkTeacher(email, password)) {
            String role = db.getTeacherRole(email);
            sessionManager.createSession(email, role);

            Intent intent;
            switch (role) {
                case "Admin":
                    intent = new Intent(this, MainActivity.class);
                    sessionManager.sendNotification("Login Successful", "Welcome Admin, " + email);

                    break;
                case "Teacher":
                    intent = new Intent(this, HomeEnseg.class);
                    sessionManager.sendNotification("Login Successful", "Welcome, " + email);

                    break;
                default:
                    intent = new Intent(this, HomeStudent.class);
                    sessionManager.sendNotification("Login Successful", "Welcome, " + email);
                    break;
            }
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, activity_register.class);
        startActivity(intent);
    }


    private void checkUserHaveNotificationPermission(){
        Log.d("MainActivity", "Checking notification permission...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Requesting notification permission...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            } else {
                Log.d("MainActivity", "Notification permission already granted.");
            }
        } else {
            Log.d("MainActivity", "Notification permission not required on this Android version.");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MainActivity", "onRequestPermissionsResult called with requestCode: " + requestCode);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted.");
                sessionManager.sendNotification("Permission Granted", "You can now receive notifications.");
            } else {
                Log.d("MainActivity", "Notification permission denied.");
                Toast.makeText(this, "Notifications permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}