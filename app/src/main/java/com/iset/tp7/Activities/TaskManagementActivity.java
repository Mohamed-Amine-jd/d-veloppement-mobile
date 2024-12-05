package com.iset.tp7.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityTaskManagementBinding;
import com.iset.tp7.Adapters.TaskAdapter;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Taskstudent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskManagementActivity extends AppCompatActivity {
    private ListView taskListView;
    private Button addTaskButton;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Taskstudent> taskList;
    private ActivityTaskManagementBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        binding = ActivityTaskManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        taskListView = findViewById(R.id.task_list_view);
        addTaskButton = findViewById(R.id.add_task_button);

        dbHelper = new DatabaseHelper(this);

        loadTasks();

        addTaskButton.setOnClickListener(v -> showAddTaskDialog());
        binding.closeDialogButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskManagementActivity.this, HomeStudent.class);
            startActivity(intent);
            finish();
        });
    }


//
    private void loadTasks() {
        taskList = dbHelper.getAllTasks();
        taskAdapter = new TaskAdapter(this, taskList, dbHelper);
        taskListView.setAdapter(taskAdapter);
        taskListView.setEmptyView(findViewById(R.id.empty_view));

        taskListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Toast.makeText(TaskManagementActivity.this, "Long click detected", Toast.LENGTH_SHORT).show();
            showDeleteTaskDialog(taskList.get(position));
            return true;
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        dialogBuilder.setView(dialogView);

        EditText taskNameEditText = dialogView.findViewById(R.id.task_name_edit);
        Button selectDateButton = dialogView.findViewById(R.id.select_date_button);
        Spinner prioritySpinner = dialogView.findViewById(R.id.priority_spinner);

        String[] priorityLevels = {"Basse", "Moyenne", "Haute"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                priorityLevels
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);

        final long[] selectedDate = {System.currentTimeMillis()};

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        selectedDate[0] = selectedCalendar.getTimeInMillis();
                        selectDateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        dialogBuilder.setTitle("Ajouter une nouvelle tâche");
        dialogBuilder.setPositiveButton("Ajouter", (dialog, whichButton) -> {
            String taskName = taskNameEditText.getText().toString().trim();
            int priorityLevel = prioritySpinner.getSelectedItemPosition() + 1;

            if (!taskName.isEmpty()) {
                Taskstudent newTask = new Taskstudent(taskName, selectedDate[0], false, priorityLevel);
                long result = dbHelper.addTask(newTask);

                if (result != -1) {
                    loadTasks();
                    Toast.makeText(this, "Tâche ajoutée", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erreur lors de l'ajout de la tâche", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Veuillez saisir un nom de tâche", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Annuler", (dialog, whichButton) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showUpdateTaskDialog(final Taskstudent task) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        dialogBuilder.setView(dialogView);

        EditText taskNameEditText = dialogView.findViewById(R.id.task_name_edit);
        Button selectDateButton = dialogView.findViewById(R.id.select_date_button);
        Spinner prioritySpinner = dialogView.findViewById(R.id.priority_spinner);

        String[] priorityLevels = {"Basse", "Moyenne", "Haute"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                priorityLevels
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);

        taskNameEditText.setText(task.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectDateButton.setText(sdf.format(new Date(task.getDueDate())));
        prioritySpinner.setSelection(task.getPriority() - 1);

        final long[] selectedDate = {task.getDueDate()};

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        selectedDate[0] = selectedCalendar.getTimeInMillis();
                        selectDateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        dialogBuilder.setTitle("Modifier la tâche");
        dialogBuilder.setPositiveButton("Mettre à jour", (dialog, whichButton) -> {
            String taskName = taskNameEditText.getText().toString().trim();
            int priorityLevel = prioritySpinner.getSelectedItemPosition() + 1;

            if (!taskName.isEmpty()) {
                task.setName(taskName);
                task.setDueDate(selectedDate[0]);
                task.setPriority(priorityLevel);

                boolean result = dbHelper.updateTask(task);

                if (result) {
                    loadTasks();
                    Toast.makeText(this, "Tâche mise à jour", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erreur lors de la mise à jour de la tâche", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Veuillez saisir un nom de tâche", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Annuler", (dialog, whichButton) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    private void showDeleteTaskDialog(final Taskstudent task) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la tâche")
                .setMessage("Voulez-vous vraiment supprimer cette tâche ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    dbHelper.deleteTask(task);
                    loadTasks();
                    Toast.makeText(this, "Tâche supprimée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}