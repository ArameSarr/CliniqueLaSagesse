package com.clinique.lasagesse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.adapters.RendezVousAdapter;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.Patient;
import com.clinique.lasagesse.models.RendezVous;
import com.clinique.lasagesse.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PatientActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RendezVousAdapter adapter;
    private FloatingActionButton fabNouveauRdv;
    private BottomNavigationView bottomNavigation;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Patient currentPatient;

    private List<RendezVous> rendezVousList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        initViews();
        initDatabase();
        setupRecyclerView();
        setupListeners();
        loadPatientData();
        loadRendezVous();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view_rdv);
        fabNouveauRdv = findViewById(R.id.fab_nouveau_rdv);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        sessionManager = new SessionManager(this);
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupRecyclerView() {
        adapter = new RendezVousAdapter(this, rendezVousList, true); // true = mode patient
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabNouveauRdv.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrendreRendezVousActivity.class);
            startActivity(intent);
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_accueil) {
                // Déjà sur l'accueil
                return true;
            } else if (itemId == R.id.nav_dossier) {
                Intent intent = new Intent(this, ConsulterDossierActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profil) {
                // Ouvrir page profil
                showToast("Profil - En développement");
                return true;
            }
            return false;
        });
    }

    private void loadPatientData() {
        int userId = sessionManager.getUserId();
        currentPatient = databaseHelper.getPatientByUserId(userId);

        if (currentPatient != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bonjour " + currentPatient.getUser().getPrenom());
        }
    }

    private void loadRendezVous() {
        if (currentPatient != null) {
            rendezVousList.clear();
            rendezVousList.addAll(databaseHelper.getRendezVousByPatient(currentPatient.getId()));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRendezVous(); // Recharger à chaque retour sur l'activité
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            loadRendezVous();
            showToast("Liste mise à jour");
            return true;
        } else if (itemId == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sessionManager.logoutUser();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}