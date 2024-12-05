package com.iset.tp7.DataBaseSQLlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iset.tp7.security.BCrypPass;
import com.iset.tp7.entities.Course;
import com.iset.tp7.entities.Taskstudent;
import com.iset.tp7.entities.Teacher;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database and Table Details
    private static final String DATABASE_NAME = "ElearningDB";
    private static final int DATABASE_VERSION = 8; // Incremented version for schema changes

    // Table Names
    private static final String TABLE_TEACHERS = "Teachers";
    private static final String TABLE_COURS = "Cours";

    // Teachers Table Columns
    private static final String COL_ID = "ID";
    private static final String COL_FIRST_NAME = "FirstName";
    private static final String COL_LAST_NAME = "LastName";
    private static final String COL_EMAIL = "Email";
    private static final String COL_PHONE = "PhoneNumber";
    private static final String COL_PASSWORD = "Password";
    private static final String COL_ROLE = "Role"; // New column for teacher role

    // Nom de la table et colonnes
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_IS_COMPLETED = "is_completed";
    private static final String COLUMN_PRIORITY = "priority";

    // Cours Table Columns
    private static final String COL_COURS_ID = "CoursID";
    private static final String COL_COURS_NAME = "CoursName";
    private static final String COL_COURS_DESCRIPTION = "CoursDescription";
    private static final String COL_COURS_PDF = "CoursPDF"; // BLOB type for storing files
    private static final String COL_COURS_TYPE = "CoursType";
    private static final String COL_COURS_ENSEIGNANT = "EnseignantName";
    private static final String COL_PHONET = "PhoneNumber";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    // Create Tables Query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Teachers Table
        String createTeachersTable = "CREATE TABLE " + TABLE_TEACHERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FIRST_NAME + " TEXT NOT NULL, " +
                COL_LAST_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PHONE + " TEXT NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_ROLE + " TEXT DEFAULT 'Teacher')";
        db.execSQL(createTeachersTable);

        String createTableCours = "CREATE TABLE IF NOT EXISTS Cours (" +
                "CoursID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CoursName TEXT NOT NULL, " +
                "CoursDescription TEXT NOT NULL, " +
                "CoursPDF BLOB, " +
                "CoursType TEXT NOT NULL, " +
                "PhoneNumber TEXT NOT NULL, " +
                "EnseignantName TEXT NOT NULL)";
        db.execSQL(createTableCours);


        // Requête de création de table
        String createTaskTable =
                "CREATE TABLE " + TABLE_TASKS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_DUE_DATE + " INTEGER, " +
                        COLUMN_IS_COMPLETED + " INTEGER, " +
                        COLUMN_PRIORITY + " INTEGER)";
        db.execSQL(createTaskTable);




    }






    // Upgrade Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }



    // Ajouter une tâche
    public long addTask(Taskstudent task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_PRIORITY, task.getPriority());

        // Adding the task and getting the ID
        long id = db.insert("tasks", null, values);

// Ensure the ID is set back to the task object
        if (id != -1) {
            task.setId((int) id);
        } else {
            Log.e("DatabaseHelper", "Error inserting task");
        }
        return id;



    }

    public List<Taskstudent> getAllTasks() {
        List<Taskstudent> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
                int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED));
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));

                Taskstudent task = new Taskstudent(name, dueDate, isCompleted == 1, priority);
                task.setId(id); // Set the retrieved ID
                taskList.add(task);
            }
            cursor.close();
        }
        return taskList;
    }



    // Delete a task from the database
    public void deleteTask(Taskstudent task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    // Update a task in the database
    public boolean updateTask(Taskstudent task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_PRIORITY, task.getPriority());

        int rowsUpdated = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();

        if (rowsUpdated > 0) {
            Log.d("DatabaseHelper", "Task updated successfully: " + task.getId());
            return true;
        } else {
            Log.e("DatabaseHelper", "Error updating task: " + task.getId());
            return false;
        }
    }
    public boolean insertTeacher(String firstName, String lastName, String email, String phone, String hashedPassword, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_LAST_NAME, lastName);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);
        values.put(COL_PASSWORD, hashedPassword); // Already hashed password
        values.put(COL_ROLE, role);

        long result = db.insert(TABLE_TEACHERS, null, values);
        db.close();
        return result != -1; // Returns true if insertion is successful
    }

    public boolean checkTeacher(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Password FROM " + TABLE_TEACHERS + " WHERE " + COL_EMAIL + "=?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(0);
            cursor.close();
            db.close();
            try {
                return BCrypPass.checkPassword(password, storedPassword); // Check the password
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error while checking password", e); // Log the error
                return false;
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return false;
        }
    }


    public Teacher getTeacherByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM teachers WHERE email = ?", new String[]{email});

        Teacher teacher = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

            teacher = new Teacher(id, firstName, lastName, email, phone, role);
        }

        cursor.close();
        db.close();
        return teacher;
    }
    // Check if Email Already Exists
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHERS + " WHERE " + COL_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Insert a New Cours
    public boolean insertCours(String coursName, String coursDescription, byte[] coursPDF, String coursType, String email,String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COURS_NAME, coursName);
        values.put(COL_COURS_DESCRIPTION, coursDescription);
        values.put(COL_COURS_PDF, coursPDF);
        values.put(COL_COURS_TYPE, coursType);
        values.put(COL_COURS_ENSEIGNANT, email);
        values.put(COL_PHONET, phone);

        long result = db.insert(TABLE_COURS, null, values);
        db.close();
        return result != -1; // Returns true if insertion is successful
    }


    public List<Course> getCoursesByTeacherEmail(String email) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURS + " WHERE " + COL_COURS_ENSEIGNANT + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURS_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_DESCRIPTION));
                byte[] pdf = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_COURS_PDF));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_TYPE));
                String teacherName = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_ENSEIGNANT));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONET));

                Course course = new Course(id, name, description, pdf, type, teacherName, phone);
                courseList.add(course);
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return courseList;
    }

    // Fetch All Courses
    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURS_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_DESCRIPTION));
                byte[] pdf = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_COURS_PDF));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_TYPE));
                String enseignantName = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_ENSEIGNANT));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONET));

                // Create a Course object and add it to the list
                courseList.add(new Course(id, name, description, pdf, type, enseignantName,phone));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courseList;
    }

    // Fetch Course by ID
    public Course getCourse(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURS + " WHERE " + COL_COURS_ID + "=?", new String[]{String.valueOf(courseId)});

        Course course = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURS_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_DESCRIPTION));
            byte[] pdf = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_COURS_PDF));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_TYPE));
            String teacherName = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURS_ENSEIGNANT));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONET));

            course = new Course(id, name, description, pdf, type, teacherName,phone);
        }

        cursor.close();
        db.close();
        return course;
    }

    // Update Course
    public boolean updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COURS_NAME, course.getName());
        values.put(COL_COURS_DESCRIPTION, course.getDescription());
        values.put(COL_COURS_PDF, course.getPdf());
        values.put(COL_COURS_TYPE, course.getType());
        values.put(COL_COURS_ENSEIGNANT, course.getTeacherName());

        int rowsUpdated = db.update(TABLE_COURS, values, COL_COURS_ID + " = ?", new String[]{String.valueOf(course.getId())});
        db.close();
        return rowsUpdated > 0;
    }

    // Delete Course
    public boolean deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_COURS, COL_COURS_ID + " = ?", new String[]{String.valueOf(courseId)});
        db.close();
        return rowsDeleted > 0;
    }

    // Fetch All Teacher Names
    public List<String> getTeacherNames() {
        List<String> teacherNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_FIRST_NAME + ", " + COL_LAST_NAME + " FROM " + TABLE_TEACHERS, null);
        if (cursor.moveToFirst()) {
            do {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                teacherNames.add(firstName + " " + lastName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return teacherNames;
    }

    // Fetch Teacher Role by Email
    public String getTeacherRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ROLE + " FROM " + TABLE_TEACHERS + " WHERE " + COL_EMAIL + "=?", new String[]{email});

        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
        }

        cursor.close();
        db.close();
        return role;
    }

    // Method to get all teachers from the Teachers table
    public List<Teacher> getAllTeachers() {
        List<Teacher> teacherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select all teachers
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHERS, null);

        // If there are teachers in the database
        if (cursor.moveToFirst()) {
            do {
                // Get data for each teacher
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));

                // Create a Teacher object and add it to the list
                teacherList.add(new Teacher(id, firstName, lastName, email, phone, role));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return teacherList;
    }

    public boolean updateStudentData(String oldEmail, String newFirstName, String newLastName, String newEmail, String newPhone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FIRST_NAME, newFirstName);
        contentValues.put(COL_LAST_NAME, newLastName);
        contentValues.put(COL_EMAIL, newEmail);
        contentValues.put(COL_PHONE, newPhone);
        contentValues.put(COL_PASSWORD, BCrypPass.hashPassword(newPassword)); // Hash the password before storing

        int result = db.update(TABLE_TEACHERS, contentValues, COL_EMAIL + " = ?", new String[]{oldEmail});
        db.close();
        return result > 0;
    }

    public Teacher getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHERS + " WHERE " + COL_EMAIL + "=?", new String[]{email});

        Teacher teacher = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));

            teacher = new Teacher(id, firstName, lastName, email, phone, password);
        }

        cursor.close();
        db.close();
        return teacher;
    }
}