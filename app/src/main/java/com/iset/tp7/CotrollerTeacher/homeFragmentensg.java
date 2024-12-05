package com.iset.tp7.CotrollerTeacher;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp7.R;
import com.example.tp7.databinding.FragmentHomeBinding;
import com.iset.tp7.Adapters.CourseAdapter;
import com.iset.tp7.Adapters.TeacherAdapter;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Course;

import java.util.ArrayList;
import java.util.List;

public class homeFragmentensg extends Fragment {

    private FragmentHomeBinding binding;
    private TeacherAdapter teacherAdapter;

    RecyclerView mRecyclerview;

    public homeFragmentensg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liste_cours, container, false);
        // Inflate the layout for this fragment using ViewBinding
//        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Set up RecyclerView with an empty list initially
        mRecyclerview = view.findViewById(R.id.recycler_view);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        teacherAdapter = new TeacherAdapter(getContext(), new ArrayList<>());
        mRecyclerview.setAdapter(teacherAdapter);

        // Load teacher data into RecyclerView
        loadTeacherData();

        return view;
    }

    private void loadTeacherData() {
        // Fetch teacher data from the database
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Course> courses = dbHelper.getAllCourses();
        if (courses != null && !courses.isEmpty()) {
            CourseAdapter courseAdapter = new CourseAdapter(getContext(), courses, new ArrayList<>());
            courseAdapter.setCourseList(courses);
            mRecyclerview.setAdapter(courseAdapter);
        } else {
            Toast.makeText(getContext(), "No Courses found.", Toast.LENGTH_SHORT).show();
        }
    }
}