package com.iset.tp7;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityGetstartedBinding;
import com.iset.tp7.Activities.HomeStudent;
import com.iset.tp7.Activities.MainActivity;
import com.iset.tp7.Auth.LoginActivity;
import com.iset.tp7.CotrollerTeacher.HomeEnseg;
import com.iset.tp7.Session.SessionManager;

import java.util.Locale;

public class Getstarted extends AppCompatActivity {
    private ActivityGetstartedBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetstartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        // Check if user is already connected


        // Go to Login Button Click Listener
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isConnected()) {
                    String role = sessionManager.getUserRole();
                    Intent intent;
                    if ("Admin".equals(role)) {
                        intent = new Intent(Getstarted.this, MainActivity.class);
                    } else if ("Teacher".equals(role)) {
                        intent = new Intent(Getstarted.this, HomeEnseg.class);
                    } else {
                        intent = new Intent(Getstarted.this, HomeStudent.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Getstarted.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Setup language buttons
        Button btnEnglish = findViewById(R.id.btnEnglish);
        Button btnFrench = findViewById(R.id.btnFrench);

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });

        btnFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("fr");
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate(); // Restart activity to apply changes
    }
    }
