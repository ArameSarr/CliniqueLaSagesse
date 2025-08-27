package com.clinique.lasagesse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.utils.SessionManager;

public class AdminActivity extends AppCompatActivity {
    private TextView tvTotalPatients, tvTotalMedecins, tvTotalRdvAujourdhui;
    private CardView cardGererRendezVous, cardStatistiques; // Retirer cardGererUtilisateurs

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initViews();
        initDatabase();
        setupListeners();
        loadStatistiques();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Administration");
        }

        tvTotalPatients = findViewById(R.id.tv_total_patients);
        tvTotalMedecins = findViewById(R.id.tv_total_medecins);
        tvTotalRdvAujourdhui = findViewById(R.id.tv_total_rdv_aujourd_hui);

        // Retirer l'initialisation de cardGererUtilisateurs
        cardGererRendezVous = findViewById(R.id.card_gerer_rendez_vous);
        cardStatistiques = findViewById(R.id.card_statistiques);

        sessionManager = new SessionManager(this);
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupListeners() {
        // Retirer l'écouteur pour cardGererUtilisateurs
        cardGererRendezVous.setOnClickListener(v -> {
            // Ouvrir gestion des rendez-vous
            showToast("Gestion rendez-vous - En développement");
        });

        cardStatistiques.setOnClickListener(v -> {
            // Ouvrir statistiques détaillées
            showToast("Statistiques - En développement");
        });
    }

    private void loadStatistiques() {
        // Charger les statistiques depuis la base de données
        int totalPatients = databaseHelper.getAllPatients().size();
        int totalMedecins = databaseHelper.getAllMedecins().size();
        int rdvAujourdhui = databaseHelper.getAllRendezVous().size(); // Simplification

        tvTotalPatients.setText(String.valueOf(totalPatients));
        tvTotalMedecins.setText(String.valueOf(totalMedecins));
        tvTotalRdvAujourdhui.setText(String.valueOf(rdvAujourdhui));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistiques();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            loadStatistiques();
            showToast("Données mises à jour");
            return true;
        } else if (itemId == R.id.action_backup) {
            // Fonction de sauvegarde
            showToast("Sauvegarde - En développement");
            return true;
        } else if (itemId == R.id.action_settings) {
            // Paramètres
            showToast("Paramètres - En développement");
            return true;
        } else if (itemId == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sessionManager.logoutUser ();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
