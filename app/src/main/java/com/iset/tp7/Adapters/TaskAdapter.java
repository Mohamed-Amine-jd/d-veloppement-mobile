package com.iset.tp7.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tp7.R;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Taskstudent;
import com.iset.tp7.Activities.TaskManagementActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<Taskstudent> {
    private final Context context;
    private final List<Taskstudent> tasks;
    private final DatabaseHelper dbHelper;

    public TaskAdapter(Context context, List<Taskstudent> tasks, DatabaseHelper dbHelper) {
        super(context, R.layout.task_item, tasks);
        this.context = context;
        this.tasks = tasks;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.task_item, parent, false);

            holder = new ViewHolder();
            holder.taskName = convertView.findViewById(R.id.task_name);
            holder.taskDate = convertView.findViewById(R.id.task_date);
            holder.taskPriority = convertView.findViewById(R.id.task_priority);
            holder.taskCompleted = convertView.findViewById(R.id.task_completed);
            holder.updateButton = convertView.findViewById(R.id.update_button);
            holder.deleteButton = convertView.findViewById(R.id.supprimer_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Taskstudent task = tasks.get(position);

        holder.taskName.setText(task.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.taskDate.setText(sdf.format(new Date(task.getDueDate())));

        String priorityText = context.getString(R.string.priority_unknown);
        int backgroundColor = context.getResources().getColor(R.color.white);  // Default color

        // Change priority text and background color
        switch (task.getPriority()) {
            case 1:  // Basse
                priorityText = context.getString(R.string.priority_low);
                backgroundColor = context.getResources().getColor(R.color.colorDivider);  // Green color for low priority
                break;
            case 2:  // Moyenne
                priorityText = context.getString(R.string.priority_medium);
                backgroundColor = context.getResources().getColor(R.color.colorPrimaryDarkLight);  // Purple color for medium priority
                break;
            case 3:  // Haute
                priorityText = context.getString(R.string.priority_high);
                backgroundColor = context.getResources().getColor(R.color.colorAccentDark);  // Red color for high priority
                break;
        }

        holder.taskPriority.setText(priorityText);
        convertView.setBackgroundColor(backgroundColor);  // Set background color based on priority

        holder.taskCompleted.setChecked(task.isCompleted());
        holder.taskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            if (task.getId() > 0) {
                dbHelper.updateTask(task);
            } else {
                Log.e("TaskAdapter", "Invalid task ID: " + task.getId());
            }
        });

        holder.updateButton.setOnClickListener(v -> {
            if (context instanceof TaskManagementActivity) {
                ((TaskManagementActivity) context).showUpdateTaskDialog(task);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteTask(task);
            tasks.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }

    static class ViewHolder {
        TextView taskName;
        TextView taskDate;
        TextView taskPriority;
        CheckBox taskCompleted;
        ImageButton updateButton;
        ImageButton deleteButton;
    }
}