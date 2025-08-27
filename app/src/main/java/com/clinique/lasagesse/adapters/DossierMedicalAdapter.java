package com.clinique.lasagesse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.models.DossierMedical;
import com.clinique.lasagesse.utils.DateUtils;

import java.util.List;

public class DossierMedicalAdapter extends RecyclerView.Adapter<DossierMedicalAdapter.ViewHolder> {
    private Context context;
    private List<DossierMedical> dossiersList;
    private OnDossierClickListener listener;

    public interface OnDossierClickListener {
        void onDossierClick(DossierMedical dossier);
        void onEditDossier(DossierMedical dossier);
    }

    public DossierMedicalAdapter(Context context, List<DossierMedical> dossiersList) {
        this.context = context;
        this.dossiersList = dossiersList;
    }

    public void setOnDossierClickListener(OnDossierClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dossier_medical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DossierMedical dossier = dossiersList.get(position);

        holder.tvDate.setText(DateUtils.formatDate(dossier.getDateConsultation()));

        if (dossier.getMedecin() != null && dossier.getMedecin().getUser() != null) {
            holder.tvMedecin.setText("Dr. " + dossier.getMedecin().getUser().getNomComplet());
        }

        holder.tvDiagnostic.setText(dossier.getDiagnostic() != null ?
                dossier.getDiagnostic() : "Diagnostic non renseigné");

        holder.tvPrescription.setText(dossier.getPrescription() != null ?
                dossier.getPrescription() : "Aucune prescription");

        if (dossier.getNotes() != null && !dossier.getNotes().trim().isEmpty()) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText(dossier.getNotes());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDossierClick(dossier);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEditDossier(dossier);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dossiersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvDate, tvMedecin, tvDiagnostic, tvPrescription, tvNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_dossier);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvMedecin = itemView.findViewById(R.id.tv_medecin);
            tvDiagnostic = itemView.findViewById(R.id.tv_diagnostic);
            tvPrescription = itemView.findViewById(R.id.tv_prescription);
            tvNotes = itemView.findViewById(R.id.tv_notes);
        }
    }
}
