package com.iset.tp7.ControllerCourse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityEditCourseBinding;
import com.iset.tp7.CotrollerTeacher.HomeEnseg;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Course;

import java.io.InputStream;
import java.io.IOException;

public class EditCourseActivity extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST = 1; // Code to identify the PDF picker intent
   private ActivityEditCourseBinding bind;
    private EditText etCourseName;
    private EditText etCourseDescription;
    private EditText etCourseType;
    private Button btnSave;
    private Button btnSelectPdf; // Button to trigger the PDF selection
    private ImageButton ibBack; // Button to go back // Button to view the selected PDF
    private Course course;
    private DatabaseHelper dbHelper;
    private byte[] selectedPdf; // To store the selected PDF as a byte array

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        bind = ActivityEditCourseBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        etCourseName = findViewById(R.id.etCourseName);
        etCourseDescription = findViewById(R.id.etCourseDescription);
        etCourseType = findViewById(R.id.etCourseType);
        btnSave = findViewById(R.id.btnSave);
        ibBack = findViewById(R.id.ibBack);
        btnSelectPdf = findViewById(R.id.btnSelectPdf); // Initialize the PDF button
        dbHelper = new DatabaseHelper(this);

        bind.ibBack.setOnClickListener(v -> {
            Intent intent = new Intent(EditCourseActivity.this, HomeEnseg.class);
            startActivity(intent);
            finish();
        });
        // Get the course data from the intent
        int courseId =getIntent().getIntExtra("courseID", -1);
        if(courseId == -1){
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            finish();
        }
        course = dbHelper.getCourse(courseId);

        // Populate the form with the existing data
        if (course != null) {
            etCourseName.setText(course.getName());
            etCourseDescription.setText(course.getDescription());
            etCourseType.setText(course.getType());
            // If the course has a PDF, you could display the file name or use a method to show it
        }

        // Set up listener for selecting a PDF file
        btnSelectPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_PDF_REQUEST);
        });

        // Save the updated course data
        btnSave.setOnClickListener(v -> {
            course.setName(etCourseName.getText().toString());
            course.setDescription(etCourseDescription.getText().toString());
            course.setType(etCourseType.getText().toString());

            // If a new PDF was selected, update the course's PDF field
            if (selectedPdf != null) {
                course.setPdf(selectedPdf); // Set the new PDF (BLOB)
            }

            // Save the updated course data to the database
            boolean isUpdated = dbHelper.updateCourse(course);
            if (isUpdated) {
                Toast.makeText(EditCourseActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditCourseActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle the result of the PDF file picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    selectedPdf = new byte[inputStream.available()];
                    inputStream.read(selectedPdf);
                    inputStream.close();
                    Toast.makeText(this, "PDF file selected", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to read PDF file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
