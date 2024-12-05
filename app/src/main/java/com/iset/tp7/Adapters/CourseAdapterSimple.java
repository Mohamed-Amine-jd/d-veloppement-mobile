package com.iset.tp7.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp7.R;
import com.iset.tp7.entities.Course;

import java.util.List;

public class CourseAdapterSimple extends RecyclerView.Adapter<CourseAdapterSimple.CourseViewHolder> {
    private List<Course> courseList;
    private OnItemClickListener onItemClickListener;

    public CourseAdapterSimple(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item_simple, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.nameTextView.setText(course.getName());
        holder.descriptionTextView.setText(course.getDescription());
        holder.typeTextView.setText(course.getType());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView typeTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.course_name);
            descriptionTextView = itemView.findViewById(R.id.course_description);
            typeTextView = itemView.findViewById(R.id.course_type);
        }
    }
}