package com.clinique.lasagesse.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.adapters.RendezVousAdapter;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.Medecin;
import com.clinique.lasagesse.models.RendezVous;
import com.clinique.lasagesse.utils.DateUtils;
import com.clinique.lasagesse.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedecinActivity extends AppCompatActivity {
    private TextView tvDateSelectionnee, tvNombreRdv;
    private RecyclerView recyclerView;
    private RendezVousAdapter adapter;
    private BottomNavigationView bottomNavigation;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Medecin currentMedecin;

    private List<RendezVous> rendezVousList = new ArrayList<>();
    private String selectedDate = DateUtils.getTodayAsString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medecin);

        initViews();
        initDatabase();
        setupRecyclerView();
        setupListeners();
        loadMedecinData();
        loadRendezVousForDate();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvDateSelectionnee = findViewById(R.id.tv_date_selectionnee);
        tvNombreRdv = findViewById(R.id.tv_nombre_rdv);
        recyclerView = findViewById(R.id.recycler_view_rdv);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        sessionManager = new SessionManager(this);

        // Afficher la date d'aujourd'hui par défaut
        tvDateSelectionnee.setText("Aujourd'hui - " + DateUtils.formatDate(new java.util.Date()));
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupRecyclerView() {
        adapter = new RendezVousAdapter(this, rendezVousList, false); // false = mode médecin
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        tvDateSelectionnee.setOnClickListener(v -> showDatePicker());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_accueil) {
                return true;
            } else if (itemId == R.id.nav_patients) {
                showToast("Liste patients - En développement");
                return true;
            } else if (itemId == R.id.nav_horaires) {
                showToast("Gestion horaires - En développement");
                return true;
            } else if (itemId == R.id.nav_profil) {
                showToast("Profil - En développement");
                return true;
            }
            return false;
        });
    }

    private void loadMedecinData() {
        int userId = sessionManager.getUserId();
        currentMedecin = databaseHelper.getMedecinByUserId(userId);

        if (currentMedecin != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dr. " + currentMedecin.getUser().getNom());
        }
    }

    private void loadRendezVousForDate() {
        if (currentMedecin != null) {
            rendezVousList.clear();
            rendezVousList.addAll(databaseHelper.getRendezVousByMedecin(currentMedecin.getId(), selectedDate));
            adapter.notifyDataSetChanged();

            tvNombreRdv.setText(rendezVousList.size() + " rendez-vous");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = DateUtils.formatDateForDatabase(calendar.getTime());
                    tvDateSelectionnee.setText(DateUtils.formatDate(calendar.getTime()));
                    loadRendezVousForDate();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRendezVousForDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_medecin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            loadRendezVousForDate();
            showToast("Liste mise à jour");
            return true;
        } else if (itemId == R.id.action_dossiers) {
            showToast("Dossiers médicaux - En développement");
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