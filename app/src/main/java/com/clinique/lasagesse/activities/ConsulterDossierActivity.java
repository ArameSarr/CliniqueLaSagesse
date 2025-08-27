package com.clinique.lasagesse.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.adapters.DossierMedicalAdapter;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.DossierMedical;
import com.clinique.lasagesse.models.Patient;
import com.clinique.lasagesse.utils.PDFGenerator;
import com.clinique.lasagesse.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ConsulterDossierActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private RecyclerView recyclerView;
    private DossierMedicalAdapter adapter;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Patient currentPatient;
    private TextView tvNombreDossiers;
    private Button btnTelechargerPdf;
    private List<DossierMedical> dossiersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_dossier);

        initViews();
        initDatabase();
        setupRecyclerView();
        loadPatientData();
        loadDossiers();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnTelechargerPdf = findViewById(R.id.btn_telecharger_pdf);
        tvNombreDossiers = findViewById(R.id.tv_nombre_dossiers);
        btnTelechargerPdf.setOnClickListener(v -> checkPermissionAndDownload());
        getSupportActionBar().setTitle("Mon dossier médical");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view_dossiers);
        sessionManager = new SessionManager(this);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupRecyclerView() {
        adapter = new DossierMedicalAdapter(this, dossiersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadPatientData() {
        int userId = sessionManager.getUserId();
        currentPatient = databaseHelper.getPatientByUserId(userId);
    }

    private void loadDossiers() {
        if (currentPatient != null) {
            dossiersList.clear();
            dossiersList.addAll(databaseHelper.getDossiersMedicauxByPatient(currentPatient.getId()));
            adapter.notifyDataSetChanged();

            if (tvNombreDossiers != null) { // Vérifie si initialisé
                tvNombreDossiers.setText(dossiersList.size() + " consultation(s) dans votre dossier");
            }
        } else {
            if (tvNombreDossiers != null) {
                tvNombreDossiers.setText("0 consultation(s) dans votre dossier");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dossier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_download) {
            checkPermissionAndDownload();
            return true;
        } else if (itemId == R.id.action_refresh) {
            loadDossiers();
            Toast.makeText(this, "Dossier mis à jour", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionAndDownload() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            downloadDossier();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadDossier();
            } else {
                Toast.makeText(this, "Permission requise pour télécharger le dossier",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadDossier() {
        if (currentPatient != null && !dossiersList.isEmpty()) {
            String filePath = PDFGenerator.generateDossierMedicalPDF(this, currentPatient, dossiersList);
            if (filePath != null) {
                Toast.makeText(this, "Dossier téléchargé: " + filePath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Aucun dossier à télécharger", Toast.LENGTH_SHORT).show();
        }
    }
}