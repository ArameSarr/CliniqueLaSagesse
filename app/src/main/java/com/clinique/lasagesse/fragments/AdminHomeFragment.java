package com.clinique.lasagesse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.activities.GererUtilisateursActivity;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.Admin;
import com.clinique.lasagesse.utils.SessionManager;

public class AdminHomeFragment extends Fragment {
    private TextView tvWelcome, tvTotalPatients, tvTotalMedecins, tvTotalRdv;
    private TextView tvRdvAujourdhui, tvRdvSemaine, tvRdvMois;
    private MaterialCardView cardGererUtilisateurs, cardGererRendezVous;
    private MaterialCardView cardRapports, cardParametres;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Admin currentAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        initViews(view);
        initDatabase();
        loadAdminData();
        setupListeners();
        loadStatistiques();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvTotalPatients = view.findViewById(R.id.tv_total_patients);
        tvTotalMedecins = view.findViewById(R.id.tv_total_medecins);


        cardGererUtilisateurs = view.findViewById(R.id.card_gerer_utilisateurs);
        cardGererRendezVous = view.findViewById(R.id.card_gerer_rendez_vous);


        sessionManager = new SessionManager(requireContext());
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(requireContext());
    }

    private void loadAdminData() {
        int userId = sessionManager.getUserId();
        // Charger les données admin (à implémenter dans DatabaseHelper si nécessaire)
        tvWelcome.setText("Administration");
    }

    private void setupListeners() {
        cardGererUtilisateurs.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GererUtilisateursActivity.class);
            startActivity(intent);
        });

        cardGererRendezVous.setOnClickListener(v -> {
            // TODO: Implémenter la gestion des rendez-vous
        });

        cardRapports.setOnClickListener(v -> {
            // TODO: Implémenter les rapports
        });

        cardParametres.setOnClickListener(v -> {
            // TODO: Implémenter les paramètres
        });
    }

    private void loadStatistiques() {
        // Charger les statistiques globales
        int totalPatients = databaseHelper.getAllPatients().size();
        int totalMedecins = databaseHelper.getAllMedecins().size();
        int totalRendezVous = databaseHelper.getAllRendezVous().size();

        tvTotalPatients.setText(String.valueOf(totalPatients));
        tvTotalMedecins.setText(String.valueOf(totalMedecins));
        tvTotalRdv.setText(String.valueOf(totalRendezVous));

        // Statistiques par période (simplifié)
        tvRdvAujourdhui.setText("12"); // À calculer réellement
        tvRdvSemaine.setText("45"); // À calculer réellement
        tvRdvMois.setText("180"); // À calculer réellement
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistiques();
    }
}