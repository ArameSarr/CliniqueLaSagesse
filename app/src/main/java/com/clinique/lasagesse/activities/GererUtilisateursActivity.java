package com.clinique.lasagesse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.adapters.PatientAdapter;
import com.clinique.lasagesse.adapters.MedecinAdapter;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.Patient;
import com.clinique.lasagesse.models.Medecin;

import java.util.ArrayList;
import java.util.List;

public class GererUtilisateursActivity extends AppCompatActivity
        implements PatientAdapter.OnPatientClickListener, MedecinAdapter.OnMedecinClickListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAjouter;

    private DatabaseHelper databaseHelper;

    private List<Patient> patientsList = new ArrayList<>();
    private List<Medecin> medecinsList = new ArrayList<>();

    private PatientAdapter patientAdapter;
    private MedecinAdapter medecinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerer_utilisateurs);

        initViews();
        initDatabase();
        setupViewPager();
        setupListeners();
        loadData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gérer les utilisateurs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tab_layout);

        fabAjouter = findViewById(R.id.fab_ajouter);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupViewPager() {
        // Configuration simple avec RecyclerViews
        // En production, utilisez un FragmentStateAdapter

        patientAdapter = new PatientAdapter(this, patientsList);
        patientAdapter.setOnPatientClickListener(this);

        medecinAdapter = new MedecinAdapter(this, medecinsList);
        medecinAdapter.setOnMedecinClickListener(this);
    }

    private void setupListeners() {
        fabAjouter.setOnClickListener(v -> {
            // Ouvrir dialog pour ajouter utilisateur
            showAddUserDialog();
        });
    }

    private void loadData() {
        patientsList.clear();
        patientsList.addAll(databaseHelper.getAllPatients());
        patientAdapter.notifyDataSetChanged();

        medecinsList.clear();
        medecinsList.addAll(databaseHelper.getAllMedecins());
        medecinAdapter.notifyDataSetChanged();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter un utilisateur")
                .setItems(new String[]{"Patient", "Médecin"}, (dialog, which) -> {
                    if (which == 0) {
                        // Ajouter patient - En développement
                        Toast.makeText(this, "Ajouter patient - En développement", Toast.LENGTH_SHORT).show();
                    } else {
                        // Ajouter médecin - En développement
                        Toast.makeText(this, "Ajouter médecin - En développement", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    public void onPatientClick(Patient patient) {
        // Voir détails patient
        Toast.makeText(this, "Détails: " + patient.getUser().getNomComplet(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditPatient(Patient patient) {
        // Modifier patient - En développement
        Toast.makeText(this, "Modifier: " + patient.getUser().getNomComplet(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeletePatient(Patient patient) {
        // Confirmer suppression
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer patient")
                .setMessage("Êtes-vous sûr de vouloir supprimer " + patient.getUser().getNomComplet() + " ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    databaseHelper.deleteUser(patient.getUserId());
                    loadData();
                    Toast.makeText(this, "Patient supprimé", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onMedecinClick(Medecin medecin) {
        // Voir détails médecin
        Toast.makeText(this, "Dr. " + medecin.getUser().getNomComplet(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditMedecin(Medecin medecin) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gerer_utilisateurs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            loadData();
            Toast.makeText(this, "Liste mise à jour", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
