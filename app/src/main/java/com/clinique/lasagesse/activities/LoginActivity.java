package com.clinique.lasagesse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.User;
import com.clinique.lasagesse.utils.SessionManager;
import com.clinique.lasagesse.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etMotDePasse;
    private Button btnConnexion;
    private ProgressBar progressBar;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initDatabase();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etMotDePasse = findViewById(R.id.et_mot_de_passe);
        btnConnexion = findViewById(R.id.btn_connexion);
        progressBar = findViewById(R.id.progress_bar);

        sessionManager = new SessionManager(this);
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupListeners() {
        btnConnexion.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String motDePasse = etMotDePasse.getText().toString();

        // Validation
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            etEmail.setError(emailError);
            etEmail.requestFocus();
            return;
        }

        String passwordError = ValidationUtils.getPasswordError(motDePasse);
        if (passwordError != null) {
            etMotDePasse.setError(passwordError);
            etMotDePasse.requestFocus();
            return;
        }

        // Afficher le progress bar
        showProgress(true);

        // Vérifier les credentials
        User user = databaseHelper.getUserByEmailAndPassword(email, motDePasse);

        showProgress(false);

        if (user != null) {
            // Connexion réussie
            sessionManager.createLoginSession(user);
            redirectToUserActivity(user.getTypeUtilisateur());
        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnConnexion.setEnabled(!show);
    }

    private void redirectToUserActivity(User.TypeUtilisateur userType) {
        Intent intent;

        switch (userType) {
            case PATIENT:
                intent = new Intent(this, PatientActivity.class);
                break;
            case MEDECIN:
                intent = new Intent(this, MedecinActivity.class);
                break;
            case ADMIN:
                intent = new Intent(this, AdminActivity.class);
                break;
            default:
                Toast.makeText(this, "Type d'utilisateur non reconnu", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
    }
}
