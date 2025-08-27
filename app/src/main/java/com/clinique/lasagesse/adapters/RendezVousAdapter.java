package com.clinique.lasagesse.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.database.DatabaseHelper;
import com.clinique.lasagesse.models.RendezVous;
import com.clinique.lasagesse.utils.DateUtils;

import java.util.List;

public class RendezVousAdapter extends RecyclerView.Adapter<RendezVousAdapter.ViewHolder> {
    private Context context;
    private List<RendezVous> rendezVousList;
    private boolean isPatientMode; // true = vue patient, false = vue médecin
    private OnRendezVousActionListener listener;

    public interface OnRendezVousActionListener {
        void onAnnulerRendezVous(RendezVous rendezVous);
        void onModifierStatut(RendezVous rendezVous);
        void onConsulterDossier(RendezVous rendezVous);
    }

    public RendezVousAdapter(Context context, List<RendezVous> rendezVousList, boolean isPatientMode) {
        this.context = context;
        this.rendezVousList = rendezVousList;
        this.isPatientMode = isPatientMode;
    }

    public void setOnRendezVousActionListener(OnRendezVousActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rendez_vous, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RendezVous rdv = rendezVousList.get(position);

        // Informations de base
        holder.tvDate.setText(DateUtils.formatDate(rdv.getDateRdv()));
        holder.tvHeure.setText(rdv.getHeureRdv());
        holder.tvMotif.setText(rdv.getMotif() != null ? rdv.getMotif() : "Consultation");

        // Affichage selon le mode (patient ou médecin)
        if (isPatientMode) {
            // Mode patient : afficher le nom du médecin
            if (rdv.getMedecin() != null && rdv.getMedecin().getUser() != null) {
                holder.tvPersonne.setText("Dr. " + rdv.getMedecin().getUser().getNomComplet());
            }

            // Boutons pour patient
            holder.btnAction1.setText("Annuler");
            holder.btnAction1.setVisibility(
                    rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE ||
                            rdv.getStatut() == RendezVous.StatutRendezVous.CONFIRME ?
                            View.VISIBLE : View.GONE
            );

            holder.btnAction2.setVisibility(View.GONE);

        } else {
            // Mode médecin : afficher le nom du patient
            if (rdv.getPatient() != null && rdv.getPatient().getUser() != null) {
                holder.tvPersonne.setText(rdv.getPatient().getUser().getNomComplet());
            }

            // Boutons pour médecin
            holder.btnAction1.setText("Confirmer");
            holder.btnAction1.setVisibility(
                    rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE ?
                            View.VISIBLE : View.GONE
            );

            holder.btnAction2.setText("Terminer");
            holder.btnAction2.setVisibility(
                    rdv.getStatut() == RendezVous.StatutRendezVous.CONFIRME ?
                            View.VISIBLE : View.GONE
            );
        }

        // Couleur de fond selon le statut
        setStatusBackground(holder.cardView, rdv.getStatut());

        // Couleur du texte statut
        holder.tvStatut.setText(rdv.getStatut().getLibelle());
        setStatusTextColor(holder.tvStatut, rdv.getStatut());

        // Listeners
        holder.btnAction1.setOnClickListener(v -> {
            if (listener != null) {
                if (isPatientMode) {
                    listener.onAnnulerRendezVous(rdv);
                } else {
                    listener.onModifierStatut(rdv);
                }
            }
        });

        holder.btnAction2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModifierStatut(rdv);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConsulterDossier(rdv);
            }
        });
    }

    private void setStatusBackground(CardView cardView, RendezVous.StatutRendezVous statut) {
        int colorRes;
        switch (statut) {
            case CONFIRME:
                colorRes = R.color.status_confirmed_background;
                break;
            case ANNULE:
                colorRes = R.color.status_cancelled_background;
                break;
            case COMPLETE:
                colorRes = R.color.statut_complete_background;
                break;
            case NO_SHOW:
                colorRes = R.color.status_noshow_background;
                break;
            case EN_ATTENTE:
            default:
                colorRes = R.color.status_pending_background;
                break;
        }
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, colorRes));
    }

    private void setStatusTextColor(TextView textView, RendezVous.StatutRendezVous statut) {
        int colorRes;
        switch (statut) {
            case EN_ATTENTE:
                colorRes = R.color.status_text_on_light_background;
                break;
            default:
                colorRes = R.color.status_text_on_colored_background;
                break;
        }
        textView.setTextColor(ContextCompat.getColor(context, colorRes));
    }

    @Override
    public int getItemCount() {
        return rendezVousList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvDate, tvHeure, tvPersonne, tvMotif, tvStatut;
        Button btnAction1, btnAction2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvDate = itemView.findViewById(R.id.tv_date);
            tvHeure = itemView.findViewById(R.id.tv_heure);
            tvPersonne = itemView.findViewById(R.id.tv_personne);
            tvMotif = itemView.findViewById(R.id.tv_motif);
            tvStatut = itemView.findViewById(R.id.tv_statut);
            btnAction1 = itemView.findViewById(R.id.btn_action1);
            btnAction2 = itemView.findViewById(R.id.btn_action2);
        }
    }
}
