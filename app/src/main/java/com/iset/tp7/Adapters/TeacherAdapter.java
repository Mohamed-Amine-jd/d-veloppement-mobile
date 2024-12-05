package com.iset.tp7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp7.R;
import com.iset.tp7.entities.Teacher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList;
    private Context context;

    public TeacherAdapter(Context context, List<Teacher> teacherList) {
        this.context = context;
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public TeacherAdapter.TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherAdapter.TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        holder.tvName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        holder.tvEmail.setText(teacher.getEmail());
        holder.position = position;

        holder.imageButtonCall.setOnClickListener(v -> {
            String phoneNumber = teacher.getPhone(); // Assuming Teacher class has getPhone() method
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(callIntent);
        });

        holder.imageButtonSms.setOnClickListener(v -> {
            String phoneNumber = teacher.getPhone(); // Assuming Teacher class has getPhone() method
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
            context.startActivity(smsIntent);
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public void sortByName() {
        teacherList.sort(Comparator.comparing(Teacher::getFirstName));
        notifyDataSetChanged();
    }

    public void reverseByName() {
        teacherList.sort((t1, t2) -> t2.getFirstName().compareTo(t1.getFirstName()));
        notifyDataSetChanged();
    }

    public void addTeacher(Teacher teacher) {
        teacherList.add(teacher);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        teacherList.remove(position);
        notifyItemRemoved(position);
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        int position;
        TextView tvName, tvEmail;
        ImageButton imageButtonCall, imageButtonSms;

        public TeacherViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            imageButtonCall = itemView.findViewById(R.id.imageButtonCall);
            imageButtonSms = itemView.findViewById(R.id.imageButtonSms);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            ((Activity) v.getContext()).getMenuInflater().inflate(R.menu.context_menu, menu);

            MenuItem deleteItem = menu.findItem(R.id.action_delete);

            // Set up the click listener for the delete option
            deleteItem.setOnMenuItemClickListener(item -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Call the method to remove the item from the list
                    TeacherAdapter adapter = (TeacherAdapter) ((RecyclerView) v.getParent()).getAdapter();
                    if (adapter != null) {
                        adapter.removeItem(position);
                    }
                    return true;
                }
                return false;
            });
        }
    }

    public void setTeachers(ArrayList<Teacher> teachers) {
        this.teacherList = teachers;
        notifyDataSetChanged();
    }

    public Teacher getTeacher(int position) {
        return teacherList.get(position);
    }

    public void removeTeacher(int position) {
        teacherList.remove(position);
        notifyItemRemoved(position);
    }
}