package com.iset.tp7.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityRegisterBinding;
import com.iset.tp7.security.BCrypPass;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;

public class activity_register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);
        setupRoleSpinner();

        binding.btnRegister.setOnClickListener(view -> registerUser());
        binding.btnGoToLogin.setOnClickListener(view -> navigateToLogin());
    }

    private void setupRoleSpinner() {
        Spinner spinnerRole = binding.spinnerRole;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void registerUser() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String role = binding.spinnerRole.getSelectedItem().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Phone number must be exactly 8 digits!", Toast.LENGTH_SHORT).show();
            return;
        }

        password = BCrypPass.hashPassword(password);

        if (db.insertTeacher(firstName, lastName, email, phone, password, role)) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } else {
            Toast.makeText(this, "Registration failed. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.length() == 8 && phone.matches("\\d+");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
