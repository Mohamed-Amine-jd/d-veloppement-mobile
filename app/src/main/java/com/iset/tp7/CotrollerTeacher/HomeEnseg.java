package com.iset.tp7.CotrollerTeacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp7.R;
import com.example.tp7.databinding.ActivityHomeEnsegBinding;
import com.google.android.material.navigation.NavigationView;
import com.iset.tp7.Activities.AboutFragment;
import com.iset.tp7.Auth.LoginActivity;
import com.iset.tp7.ControllerCourse.AddCoursFragment;
import com.iset.tp7.ControllerCourse.ListCourseTeacher;
import com.iset.tp7.ControllerCourse.ListeCoursFragment;
import com.iset.tp7.Session.SessionManager;

public class HomeEnseg extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityHomeEnsegBinding bind;
   private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityHomeEnsegBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        sessionManager = new SessionManager(this);
        setContentView(view);
        String identifiant = getIntent().getStringExtra("email");
        Toast.makeText(getApplicationContext(), "Welcome " + identifiant, Toast.LENGTH_SHORT).show();



        setTitle("Home Enseignant");

        // Initialiser la Toolbar
        setSupportActionBar(bind.toolbar);

        // Gestion des clics dans le Navigation Drawer
        bind.navView.setNavigationItemSelectedListener(this);

        // Initialiser le DrawerLayout
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragmentensg()).commit();
        } else if (id == R.id.nav_courses) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddCoursFragment()).commit();
        } else if (id == R.id.nav_list_courses) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListCourseTeacher()).commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        }else if (item.getItemId() == R.id.nav_logout) {
            sessionManager.logout();
            Log.i("tag", "exit");
            Toast.makeText(this, "Exit", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeEnseg.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        bind.drawerLayout.closeDrawer(bind.navView);
        return true;
    }
}