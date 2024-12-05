package com.iset.tp7.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp7.R;
import com.iset.tp7.entities.Course;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.ControllerCourse.EditCourseActivity;
import com.iset.tp7.entities.Teacher;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courseList;
    private OnCourseClickListener onCourseClickListener;
    private List<Teacher> teacherList;
    private DatabaseHelper dbHelper;
    private Context context;

    public CourseAdapter(Context context, List<Course> courseList, List<Teacher> teacherList) {
        this.context = context;
        this.courseList = courseList;
        this.teacherList = teacherList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cours_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        // Bind course data to UI elements
        holder.nameTextView.setText(course.getName());
        holder.descriptionTextView.setText(course.getDescription());
        holder.typeTextView.setText(course.getType());
        holder.teacherEmailTextView.setText(course.getTeacherName());

        // Populate the teacher spinner
        ArrayAdapter<Teacher> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                android.R.layout.simple_spinner_item, teacherList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.teacherSpinner.setAdapter(adapter);

        // Set the selected teacher
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getFirstName().equals(course.getTeacherName())) {
                holder.teacherSpinner.setSelection(i);
                break;
            }
        }

        // Edit button functionality
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCourseActivity.class);
            intent.putExtra("courseID", course.getId());
            context.startActivity(intent);
        });

        // Delete button functionality
        holder.btnDelete.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteCourse(course.getId());
            if (isDeleted) {
                courseList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, courseList.size());
                Toast.makeText(v.getContext(), "Course deleted: " + course.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Failed to delete course.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnPdf.setOnClickListener(v -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onCoursePdfClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView typeTextView;
        public TextView teacherEmailTextView;
        public Spinner teacherSpinner;
        public Button btnEdit;
        public Button btnDelete;
        public Button btnPdf;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.cours_nom);
            descriptionTextView = itemView.findViewById(R.id.cours_description);
            typeTextView = itemView.findViewById(R.id.cours_type);
            teacherEmailTextView = itemView.findViewById(R.id.teacher_email);
            teacherSpinner = itemView.findViewById(R.id.teacher_spinner);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnPdf = itemView.findViewById(R.id.btn_pdf);
        }
    }

    public void addCourse(Course course) {
        courseList.add(course);
        notifyDataSetChanged();
    }

    public void removeCourse(int position) {
        courseList.remove(position);
        notifyItemRemoved(position);
    }

    public void sortByName() {
        courseList.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
        notifyDataSetChanged();
    }

    public void reverseByName() {
        courseList.sort((c1, c2) -> c2.getName().compareTo(c1.getName()));
        notifyDataSetChanged();
    }

    public void sortByType() {
        courseList.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));
        notifyDataSetChanged();
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged(); // Assurez-vous que cette ligne est présente
    }



    public interface OnCourseClickListener {
        void onCoursePdfClick(Course course);
    }

    public void setOnCourseClickListener(OnCourseClickListener onCourseClickListener) {
        this.onCourseClickListener = onCourseClickListener;
    }
}