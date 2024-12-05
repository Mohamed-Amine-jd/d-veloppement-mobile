package com.iset.tp7.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.iset.tp7.Adapters.TeacherAdapter;
import com.iset.tp7.Auth.LoginActivity;
import com.iset.tp7.ControllerCourse.ListeCoursFragment;
import com.iset.tp7.CotrollerTeacher.homeFragment;
import com.iset.tp7.MusicService;
import com.iset.tp7.Session.SessionManager;
import com.iset.tp7.entities.Teacher;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding bind;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);
        setTitle("My App");

// Start the music service when the activity is created
      Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);


        sessionManager = new SessionManager(this);
        // Initialiser la Toolbar
        setSupportActionBar(bind.toolbar);

        // Gestion des clics dans le Navigation Drawer
        bind.navView.setNavigationItemSelectedListener(this);
        // Initialiser le DrawerLayout
        // Ajouter un Listener pour ouvrir/fermer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, bind.drawerLayout, bind.toolbar, R.string.open_drawer, R.string.close_drawer);
        bind.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Charger le fragment sélectionné
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
            bind.navView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            bind.toolbar.setTitle("Enseignants");
            enseig = true;
            about = false;
            invalidateOptionsMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragment()).commit();
        } else if (item.getItemId() == R.id.showcourses) {
            bind.toolbar.setTitle("Courses");
            enseig = true;
            about = false;
            invalidateOptionsMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListeCoursFragment()).commit();
        }

        else if (item.getItemId() == R.id.nav_about) {
            bind.toolbar.setTitle("About");
            about = true;
            enseig = false;
            invalidateOptionsMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (item.getItemId() == R.id.nav_logout) {
            sessionManager.logout();
            Log.i("tag", "exit");
            Toast.makeText(this, "Exit", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        bind.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (bind.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            bind.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menus) {
        getMenuInflater().inflate(R.menu.menu_main_about, menus); // Initialement "About"
        return super.onCreateOptionsMenu(menus);
    }

    private boolean enseig = false;
    private boolean about = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear(); // Efface les éléments du menu existant
        if (enseig) {
            getMenuInflater().inflate(R.menu.menu_main_enseig, menu); // Menu enseignants
        } else if (about) {
            getMenuInflater().inflate(R.menu.menu_main_about, menu); // Menu "About"
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        homeFragment fragment = (homeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        TeacherAdapter adapter = fragment != null ? fragment.getAdapter() : null;
        if (adapter != null) {
            int itemId = item.getItemId(); // Save item ID in a variable to avoid the constant expression error
            if (itemId == R.id.a_z) {
                adapter.sortByName(); // Sort by name A-Z
                return true;
            } else if (itemId == R.id.z_a) {
                adapter.reverseByName(); // Sort by name Z-A
                return true;
            } else if (itemId == R.id.add) {
                showAddingDialog(); // Show add dialog
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    // Stop the music service when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent musicIntent = new Intent(this, MusicService.class);
        stopService(musicIntent);

    }

    private void showAddingDialog() {
        MyDialogFragment dialog = new MyDialogFragment();
        dialog.setListener(new MyDialogFragment.OnTeacherAddedListener() {
            @Override
            public void onTeacherAdded(String name, String email) {
                // Create a new Teacher object with the provided name and email
                Teacher newTeacher = new Teacher(0, name, "", email, "", "");

                // Get the adapter from the homeFragment and add the new teacher
                homeFragment fragment = (homeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                TeacherAdapter adapter = fragment != null ? fragment.getAdapter() : null;

                if (adapter != null) {
                    adapter.addTeacher(newTeacher);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "add_teacher_dialog");
    }
}