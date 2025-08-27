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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.activities.PrendreRendezVousActivity;
import com.clinique.lasagesse.activities.ConsulterDossierActivity;
import com.clinique.lasagesse.adapters.RendezVousAdapter;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.Patient;
import com.clinique.lasagesse.models.RendezVous;
import com.clinique.lasagesse.utils.DateUtils;
import com.clinique.lasagesse.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PatientHomeFragment extends Fragment {
    private TextView tvWelcome, tvProchainRdv, tvNombreRdvTotal;
    private RecyclerView recyclerProchainRdv;
    private MaterialCardView cardPrendreRdv, cardConsulterDossier, cardHistorique;
    private FloatingActionButton fabNouveauRdv;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Patient currentPatient;
    private RendezVousAdapter adapter;
    private List<RendezVous> prochainRdvList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);

        initViews(view);
        initDatabase();
        loadPatientData();
        setupRecyclerView();
        setupListeners();
        loadProchainRendezVous();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvProchainRdv = view.findViewById(R.id.tv_prochain_rdv);
        tvNombreRdvTotal = view.findViewById(R.id.tv_nombre_rdv_total);
        recyclerProchainRdv = view.findViewById(R.id.recycler_prochain_rdv);
        cardPrendreRdv = view.findViewById(R.id.card_prendre_rdv);
        cardConsulterDossier = view.findViewById(R.id.card_consulter_dossier);
        cardHistorique = view.findViewById(R.id.card_historique);
        fabNouveauRdv = view.findViewById(R.id.fab_nouveau_rdv);

        sessionManager = new SessionManager(requireContext());
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(requireContext());
    }

    private void loadPatientData() {
        int userId = sessionManager.getUserId();
        currentPatient = databaseHelper.getPatientByUserId(userId);

        if (currentPatient != null && currentPatient.getUser() != null) {
            tvWelcome.setText("Bonjour, " + currentPatient.getUser().getPrenom() + " !");

            // Charger les statistiques
            List<RendezVous> allRdv = databaseHelper.getRendezVousByPatient(currentPatient.getId());
            tvNombreRdvTotal.setText(allRdv.size() + " rendez-vous au total");
        }
    }

    private void setupRecyclerView() {
        adapter = new RendezVousAdapter(requireContext(), prochainRdvList, true);
        recyclerProchainRdv.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerProchainRdv.setAdapter(adapter);
    }

    private void setupListeners() {
        cardPrendreRdv.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PrendreRendezVousActivity.class);
            startActivity(intent);
        });

        cardConsulterDossier.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ConsulterDossierActivity.class);
            startActivity(intent);
        });

        cardHistorique.setOnClickListener(v -> {
            // TODO: Implémenter la vue historique
        });

        fabNouveauRdv.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PrendreRendezVousActivity.class);
            startActivity(intent);
        });
    }

    private void loadProchainRendezVous() {
        if (currentPatient != null) {
            List<RendezVous> allRdv = databaseHelper.getRendezVousByPatient(currentPatient.getId());

            // Filtrer les prochains rendez-vous (non annulés et dans le futur)
            prochainRdvList.clear();
            for (RendezVous rdv : allRdv) {
                if (rdv.getStatut() != RendezVous.StatutRendezVous.ANNULE &&
                        rdv.getStatut() != RendezVous.StatutRendezVous.COMPLETE &&
                        DateUtils.isDateInFuture(rdv.getDateRdv())) {
                    prochainRdvList.add(rdv);
                }
            }

            if (prochainRdvList.isEmpty()) {
                tvProchainRdv.setText("Aucun rendez-vous à venir");
            } else {
                tvProchainRdv.setText("Vos prochains rendez-vous");
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentPatient != null) {
            loadProchainRendezVous();
        }
    }
}