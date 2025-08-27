package com.clinique.lasagesse.fragments;

import android.app.DatePickerDialog;
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

public class MedecinHomeFragment extends Fragment {
    private TextView tvWelcome, tvDateSelectionnee, tvNombreRdv, tvStatistiques;
    private RecyclerView recyclerRdvAujourdhui;
    private MaterialCardView cardSelectDate, cardStatistiques;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Medecin currentMedecin;
    private RendezVousAdapter adapter;
    private List<RendezVous> rendezVousAujourdhui = new ArrayList<>();
    private String selectedDate = DateUtils.getTodayAsString();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medecin_home, container, false);

        initViews(view);
        initDatabase();
        loadMedecinData();
        setupRecyclerView();
        setupListeners();
        loadRendezVousForDate();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvDateSelectionnee = view.findViewById(R.id.tv_date_selectionnee);
        tvNombreRdv = view.findViewById(R.id.tv_nombre_rdv);
        tvStatistiques = view.findViewById(R.id.tv_statistiques);
        recyclerRdvAujourdhui = view.findViewById(R.id.recycler_rdv_aujourd_hui);
        cardSelectDate = view.findViewById(R.id.card_select_date);
        cardStatistiques = view.findViewById(R.id.card_statistiques);

        sessionManager = new SessionManager(requireContext());

        // Afficher la date d'aujourd'hui par défaut
        tvDateSelectionnee.setText("Aujourd'hui - " + DateUtils.formatDate(new java.util.Date()));
    }

    private void initDatabase() {
        databaseHelper = DatabaseHelper.getInstance(requireContext());
    }

    private void loadMedecinData() {
        int userId = sessionManager.getUserId();
        currentMedecin = databaseHelper.getMedecinByUserId(userId);

        if (currentMedecin != null && currentMedecin.getUser() != null) {
            tvWelcome.setText("Dr. " + currentMedecin.getUser().getNom());

            // Charger les statistiques générales
            loadStatistiques();
        }
    }

    private void setupRecyclerView() {
        adapter = new RendezVousAdapter(requireContext(), rendezVousAujourdhui, false);
        recyclerRdvAujourdhui.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRdvAujourdhui.setAdapter(adapter);
    }

    private void setupListeners() {
        cardSelectDate.setOnClickListener(v -> showDatePicker());

        tvDateSelectionnee.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
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

    private void loadRendezVousForDate() {
        if (currentMedecin != null) {
            rendezVousAujourdhui.clear();
            rendezVousAujourdhui.addAll(databaseHelper.getRendezVousByMedecin(currentMedecin.getId(), selectedDate));

            tvNombreRdv.setText(rendezVousAujourdhui.size() + " rendez-vous");
            adapter.notifyDataSetChanged();
        }
    }

    private void loadStatistiques() {
        if (currentMedecin != null) {
            // Calculer les statistiques
            List<RendezVous> tousRdv = databaseHelper.getAllRendezVous();
            int rdvMedecin = 0;
            int rdvTermines = 0;

            for (RendezVous rdv : tousRdv) {
                if (rdv.getMedecinId() == currentMedecin.getId()) {
                    rdvMedecin++;
                    if (rdv.getStatut() == RendezVous.StatutRendezVous.COMPLETE) {
                        rdvTermines++;
                    }
                }
            }

            String stats = "Total RDV: " + rdvMedecin + " | Terminés: " + rdvTermines;
            tvStatistiques.setText(stats);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentMedecin != null) {
            loadRendezVousForDate();
            loadStatistiques();
        }
    }
}
