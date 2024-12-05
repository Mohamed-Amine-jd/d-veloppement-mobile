package com.iset.tp7.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityHomeStudentBinding;
import com.iset.tp7.Adapters.CourseAdapterSimple;
import com.iset.tp7.Auth.LoginActivity;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.Session.SessionManager;
import com.iset.tp7.entities.Course;
import com.iset.tp7.entities.Teacher;

import java.util.ArrayList;
import java.util.List;

public class HomeStudent extends AppCompatActivity {
    private SessionManager sessionManager;
    private ActivityHomeStudentBinding binding;
    private RecyclerView recyclerViewCourses;
    private CourseAdapterSimple courseAdapter;
    private DatabaseHelper dbHelper;
    private List<Course> courseList;
    private List<Course> filteredCourseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home Enseignant");
        // Binding setup
        binding = ActivityHomeStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        setupRecyclerView();
        setupListeners();
        loadCourses();

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            setupWelcomeMessage(email);
            setupProfileUpdate(email);
        }

        setupSearchView();
    }

    // Initialize and configure RecyclerView
    private void setupRecyclerView() {
        recyclerViewCourses = binding.recyclerViewCourses;
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
    }

    // Setup click listeners for logout and task management
    private void setupListeners() {
        binding.delete.setOnClickListener(v -> logout());
        binding.taskmanagement.setOnClickListener(v -> navigateToTaskManagement());
    }

    private void loadCourses() {
        courseList = dbHelper.getAllCourses();
        filteredCourseList = new ArrayList<>(courseList);

        if (courseList != null && !courseList.isEmpty()) {
            courseAdapter = new CourseAdapterSimple(filteredCourseList);
            courseAdapter.setOnItemClickListener(this::showCourseDetailsDialog);
            recyclerViewCourses.setAdapter(courseAdapter);
        } else {
            Toast.makeText(this, "No courses available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupWelcomeMessage(String email) {
        TextView textView = findViewById(R.id.textView11);
        textView.setText("Hi, Mr " + email);
    }

    private void setupProfileUpdate(String email) {
        ImageView imageView = findViewById(R.id.imageView7);
        imageView.setOnClickListener(v -> showUpdateDialog(email));
    }

    private void setupSearchView() {
        binding.searchcourse.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCourses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCourses(newText);
                return false;
            }
        });
    }

    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HomeStudent.this, LoginActivity.class));
        finish();
    }

    private void navigateToTaskManagement() {
        startActivity(new Intent(HomeStudent.this, TaskManagementActivity.class));
    }

    private void filterCourses(String query) {
        filteredCourseList.clear();
        for (Course course : courseList) {
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredCourseList.add(course);
            }
        }
        courseAdapter.notifyDataSetChanged();
    }

    private void showCourseDetailsDialog(Course course) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_course_details);

        TextView courseName = dialog.findViewById(R.id.courseName);
        TextView courseDescription = dialog.findViewById(R.id.courseDescription);
        TextView emailTeacher = dialog.findViewById(R.id.emailTeacher);
        TextView courseType = dialog.findViewById(R.id.courseType);
        Button sendEmailButton = dialog.findViewById(R.id.sendEmailButton);
        Button callPhoneButton = dialog.findViewById(R.id.callphone);

        courseName.setText(course.getName());
        courseDescription.setText(course.getDescription());
        emailTeacher.setText(course.getTeacherName());
        courseType.setText(course.getType());

        setupEmailButton(sendEmailButton, course);
        setupCallButton(callPhoneButton, course);

        dialog.show();
    }

    private void setupEmailButton(Button button, Course course) {
        button.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{course.getTeacherName()});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Course Inquiry: " + course.getName());
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Teacher,\n\nI have a question about the course " + course.getName() + ".\n\nBest regards,\n[Your Name]");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCallButton(Button button, Course course) {
        button.setOnClickListener(v -> {
            if (course.getPhone() != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + course.getPhone()));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Phone number not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(String email) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update_student);

        EditText firstNameEditText = dialog.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = dialog.findViewById(R.id.lastNameEditText);
        EditText emailEditText = dialog.findViewById(R.id.emailEditText);
        EditText phoneEditText = dialog.findViewById(R.id.phoneEditText);
        EditText passwordEditText = dialog.findViewById(R.id.passwordEditText);
        Button updateButton = dialog.findViewById(R.id.updateButton);

        Teacher student = dbHelper.getUserByEmail(email);
        if (student != null) {
            firstNameEditText.setText(student.getFirstName());
            lastNameEditText.setText(student.getLastName());
            emailEditText.setText(student.getEmail());
            phoneEditText.setText(student.getPhone());
        }

        updateButton.setOnClickListener(v -> {
            String newFirstName = firstNameEditText.getText().toString();
            String newLastName = lastNameEditText.getText().toString();
            String newEmail = emailEditText.getText().toString();
            String newPhone = phoneEditText.getText().toString();
            String newPassword = passwordEditText.getText().toString();

            boolean isUpdated = dbHelper.updateStudentData(email, newFirstName, newLastName, newEmail, newPhone, newPassword);
            if (isUpdated) {
                Toast.makeText(this, "Student data updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update student data", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }
}
