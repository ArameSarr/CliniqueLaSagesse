package com.clinique.lasagesse.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.Medecin;
import com.clinique.lasagesse.models.Patient;
import com.clinique.lasagesse.models.RendezVous;
import com.clinique.lasagesse.utils.DateUtils;
import com.clinique.lasagesse.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrendreRendezVousActivity extends AppCompatActivity {
    private AutoCompleteTextView spinnerMedecin, spinnerCreneau;
    private TextInputEditText etDate, etMotif;
    private Button btnPrendreRdv;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Patient currentPatient;

    private List<Medecin> medecinsList = new ArrayList<>();
    private List<String> creneauxDisponibles = new ArrayList<>();
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prendre_rendez_vous);

        initViews();
        initDatabase();
        setupListeners();
        loadMedecins();
        loadCurrentPatient();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nouveau Rendez-vous");

        spinnerMedecin = findViewById(R.id.spinner_medecin);
        spinnerCreneau = findViewById(R.id.spinner_creneau);
        etDate = findViewById(R.id.et_date);
        etMotif = findViewById(R.id.et_motif);
        btnPrendreRdv = findViewById(R.id.btn_prendre_rdv);

        sessionManager = new SessionManager(this);
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupListeners() {
        etDate.setOnClickListener(v -> showDatePicker());
        etDate.setFocusable(false);
        etDate.setClickable(true);

        btnPrendreRdv.setOnClickListener(v -> prendreRendezVous());

        spinnerMedecin.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < medecinsList.size() && !selectedDate.isEmpty()) {
                loadCreneauxDisponibles();
            }
        });
    }

    private void loadCurrentPatient() {
        int userId = sessionManager.getUserId();
        currentPatient = databaseHelper.getPatientByUserId(userId);
    }

    private void loadMedecins() {
        medecinsList.clear();
        medecinsList.addAll(databaseHelper.getAllMedecins());

        List<String> medecinNames = new ArrayList<>();
        for (Medecin medecin : medecinsList) {
            medecinNames.add("Dr. " + medecin.getUser().getNomComplet() + " - " + medecin.getSpecialite());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, medecinNames);
        spinnerMedecin.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDateObj = calendar.getTime();

                    // Vérifier que la date est dans le futur
                    if (!DateUtils.isDateInFuture(selectedDateObj) && !DateUtils.isToday(selectedDateObj)) {
                        Toast.makeText(this, "Veuillez sélectionner une date future", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedDate = DateUtils.formatDateForDatabase(selectedDateObj);
                    etDate.setText(DateUtils.formatDate(selectedDateObj));

                    // Recharger les créneaux si un médecin est sélectionné
                    if (!spinnerMedecin.getText().toString().isEmpty()) {
                        loadCreneauxDisponibles();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Définir la date minimum à aujourd'hui
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void loadCreneauxDisponibles() {
        String medecinText = spinnerMedecin.getText().toString();
        if (medecinText.isEmpty() || selectedDate.isEmpty()) return;

        // Trouver le médecin sélectionné
        Medecin selectedMedecin = null;
        for (int i = 0; i < medecinsList.size(); i++) {
            String medecinName = "Dr. " + medecinsList.get(i).getUser().getNomComplet() + " - " + medecinsList.get(i).getSpecialite();
            if (medecinName.equals(medecinText)) {
                selectedMedecin = medecinsList.get(i);
                break;
            }
        }

        if (selectedMedecin == null) return;

        creneauxDisponibles.clear();
        creneauxDisponibles.addAll(databaseHelper.getCreneauxDisponibles(selectedMedecin.getId(), selectedDate));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, creneauxDisponibles);
        spinnerCreneau.setAdapter(adapter);
    }

    private void prendreRendezVous() {
        // Validation
        String medecinText = spinnerMedecin.getText().toString();
        if (medecinText.isEmpty()) {
            spinnerMedecin.setError("Veuillez sélectionner un médecin");
            return;
        }

        if (selectedDate.isEmpty()) {
            etDate.setError("Veuillez sélectionner une date");
            return;
        }

        String creneauText = spinnerCreneau.getText().toString();
        if (creneauText.isEmpty()) {
            spinnerCreneau.setError("Veuillez sélectionner un créneau");
            return;
        }

        if (currentPatient == null) {
            Toast.makeText(this, "Erreur: Patient non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trouver le médecin sélectionné
        Medecin selectedMedecin = null;
        for (Medecin medecin : medecinsList) {
            String medecinName = "Dr. " + medecin.getUser().getNomComplet() + " - " + medecin.getSpecialite();
            if (medecinName.equals(medecinText)) {
                selectedMedecin = medecin;
                break;
            }
        }

        if (selectedMedecin == null) {
            Toast.makeText(this, "Erreur: Médecin non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer le rendez-vous
        try {
            Date dateRdv = DateUtils.parseDatabaseDate(selectedDate);
            String motif = etMotif.getText().toString().trim();

            if (motif.isEmpty()) {
                motif = "Consultation";
            }

            RendezVous rendezVous = new RendezVous(
                    currentPatient.getId(),
                    selectedMedecin.getId(),
                    dateRdv,
                    creneauText,
                    motif
            );

            long result = databaseHelper.ajouterRendezVous(rendezVous);

            if (result != -1) {
                Toast.makeText(this, "Rendez-vous pris avec succès!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la prise du rendez-vous", Toast.LENGTH_SHORT).show();
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Erreur de format de date", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}