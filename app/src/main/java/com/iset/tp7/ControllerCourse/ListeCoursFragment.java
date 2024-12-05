package com.iset.tp7.ControllerCourse;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp7.R;
import com.iset.tp7.Adapters.CourseAdapter;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Course;
import com.iset.tp7.entities.Teacher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListeCoursFragment extends Fragment implements CourseAdapter.OnCourseClickListener {
    private RecyclerView recyclerViewCours;
    private CourseAdapter coursAdapter;
    private List<Teacher> teacherList;
    private List<Course> coursList;
    private List<Course> filteredCoursList; // For filtering results
    private List<File> generatedPdfFiles = new ArrayList<>();
    private SearchView searchView ;
    private byte[] documentBytes;
    private String email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liste_cours, container, false);

        // Initialize RecyclerView
        recyclerViewCours = view.findViewById(R.id.recycler_view);
        recyclerViewCours.setLayoutManager(new LinearLayoutManager(getContext()));
       //email = getActivity().getIntent().getStringExtra("email");
        // Initialize DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        coursList = dbHelper.getAllCourses();
        //coursList = dbHelper.getCoursesByTeacherEmail(email);
        filteredCoursList = new ArrayList<>(coursList); // Copy for filtering
        teacherList = dbHelper.getAllTeachers();

        // Initialize Adapter
        coursAdapter = new CourseAdapter(getContext(), filteredCoursList, teacherList);
        coursAdapter.setOnCourseClickListener(this);
        recyclerViewCours.setAdapter(coursAdapter);

        // Setup Search View
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchView", "Query submitted: " + query);
                filterCourses(query);
                return true; // Indicate the query has been handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchView", "Query text changed: " + newText);
                filterCourses(newText); // Filter as user types
                return true;
            }
        });

        return view;
    }

    private void filterCourses(String query) {
        filteredCoursList.clear();
        for (Course course : coursList) {
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredCoursList.add(course);
            }
        }
        coursAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCoursePdfClick(Course course) {
        Toast.makeText(getContext(), "Opening PDF for course: " + course.getName(), Toast.LENGTH_SHORT).show();
        documentBytes = course.getPdf();
        viewPdf();
    }

    private void viewPdf() {
        if (documentBytes != null) {
            try {
                File appFolder = new File(
                        requireContext().getExternalFilesDir(null),
                        "Education"
                );

                if (!appFolder.exists() && !appFolder.mkdirs()) {
                    Toast.makeText(getContext(), "Failed to create folder", Toast.LENGTH_SHORT).show();
                    return;
                }

                File pdfFile = new File(appFolder, "Document-" + (new Date()).getTime() + ".pdf");
                FileOutputStream fos = new FileOutputStream(pdfFile);
                fos.write(documentBytes);
                fos.close();

                generatedPdfFiles.add(pdfFile);

                Uri pdfUri = FileProvider.getUriForFile(
                        getContext(),
                        requireContext().getPackageName() + ".provider",
                        pdfFile
                );

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(pdfUri, "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);

            } catch (IOException e) {
                Toast.makeText(getContext(), "Error saving or opening PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "No document available", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteGeneratedFiles() {
        for (File file : generatedPdfFiles) {
            if (file.exists() && !file.delete()) {
                Log.e("PDF Cleanup", "Failed to delete file: " + file.getAbsolutePath());
            }
        }
        generatedPdfFiles.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        deleteGeneratedFiles();
    }
}
