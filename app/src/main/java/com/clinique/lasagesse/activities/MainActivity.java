package com.clinique.lasagesse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.clinique.lasagesse.R;
import com.clinique.lasagesse.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private static final int SPLASH_TIME_OUT = 2000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Délai pour le splash screen
        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                // Rediriger vers l'activité appropriée selon le type d'utilisateur
                redirectToUserActivity();
            } else {
                // Rediriger vers la page de connexion
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_TIME_OUT);
    }

    private void redirectToUserActivity() {
        String userType = sessionManager.getUserType();
        Intent intent;

        switch (userType) {
            case "PATIENT":
                intent = new Intent(this, PatientActivity.class);
                break;
            case "MEDECIN":
                intent = new Intent(this, MedecinActivity.class);
                break;
            case "ADMIN":
                intent = new Intent(this, AdminActivity.class);
                break;
            default:
                intent = new Intent(this, LoginActivity.class);
                break;
        }

        startActivity(intent);
    }
}