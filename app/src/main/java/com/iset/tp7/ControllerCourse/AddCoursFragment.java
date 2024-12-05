package com.iset.tp7.ControllerCourse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp7.databinding.ActivityAddCoursFragmentBinding;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Course;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class AddCoursFragment extends Fragment {

    private ActivityAddCoursFragmentBinding binding;
    private static final int PICK_PDF_REQUEST = 1;
    private byte[] selectedPdf = null; // For storing the picked PDF as byte[]

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityAddCoursFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        populateTeacherSpinner();

        binding.selectPdfButton.setOnClickListener(v -> {
            // Open file picker to select PDF
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_PDF_REQUEST);
        });

        binding.addButton.setOnClickListener(v -> {
            String name = binding.nomCoursEditText.getText().toString();
            String email = binding.emailteacher.getText().toString();
            String phone = binding.phone.getText().toString();
            String description = binding.descriptionEditText.getText().toString();
            int selectedTypeId = binding.typeRadioGroup.getCheckedRadioButtonId();
            Log.d( "name: ",name);
            Log.d( "email: ",email);



            String type = selectedTypeId != -1
                    ? ((RadioButton) view.findViewById(selectedTypeId)).getText().toString()
                    : null;
            String teacherName = (String) binding.teacherSpinner.getSelectedItem();

            // Validate inputs
            if (name.isEmpty() || description.isEmpty() || type == null || email == null || selectedPdf == null) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Course object
            Course course = new Course(0, name, description, selectedPdf, type, email, phone);

            // Save to database
            try (DatabaseHelper dbHelper = new DatabaseHelper(requireContext())) {
                dbHelper.insertCours(name, description, selectedPdf, type, email, phone);
                Toast.makeText(requireContext(), "Cours ajouté avec succès", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });

        return view;
    }

    private void populateTeacherSpinner() {
        try (DatabaseHelper dbHelper = new DatabaseHelper(requireContext())) {
            List<String> teacherList = dbHelper.getTeacherNames(); // Assuming it returns List<String>
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, teacherList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.teacherSpinner.setAdapter(adapter);

            if (teacherList.isEmpty()) {
                Toast.makeText(requireContext(), "Aucun enseignant disponible", Toast.LENGTH_SHORT).show();
                binding.addButton.setEnabled(false);
            }
        }
    }

    private void clearFields() {
        binding.nomCoursEditText.setText("");
        binding.descriptionEditText.setText("");
        binding.typeRadioGroup.clearCheck();
        binding.teacherSpinner.setSelection(0);
        selectedPdf = null;
        binding.pdfTextView.setText("Aucun fichier sélectionné");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                selectedPdf = outputStream.toByteArray();
                binding.pdfTextView.setText("Fichier sélectionné: " + uri.getLastPathSegment());
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Erreur lors de la sélection du fichier", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
