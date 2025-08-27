package com.clinique.lasagesse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.models.Medecin;

import java.util.List;

public class MedecinAdapter extends RecyclerView.Adapter<MedecinAdapter.ViewHolder> {
    private Context context;
    private List<Medecin> medecinsList;
    private OnMedecinClickListener listener;

    public interface OnMedecinClickListener {
        void onMedecinClick(Medecin medecin);
        void onEditMedecin(Medecin medecin);
    }

    public MedecinAdapter(Context context, List<Medecin> medecinsList) {
        this.context = context;
        this.medecinsList = medecinsList;
    }

    public void setOnMedecinClickListener(OnMedecinClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medecin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medecin medecin = medecinsList.get(position);

        if (medecin.getUser() != null) {
            holder.tvNom.setText("Dr. " + medecin.getUser().getNomComplet());
            holder.tvEmail.setText(medecin.getUser().getEmail());
            holder.tvTelephone.setText(medecin.getUser().getTelephone());
        }

        holder.tvSpecialite.setText(medecin.getSpecialite());
        holder.tvHoraires.setText(medecin.getHorairesDebut() + " - " + medecin.getHorairesFin());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMedecinClick(medecin);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEditMedecin(medecin);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return medecinsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvEmail, tvTelephone, tvSpecialite, tvHoraires;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tv_nom);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvTelephone = itemView.findViewById(R.id.tv_telephone);
            tvSpecialite = itemView.findViewById(R.id.tv_specialite);
            tvHoraires = itemView.findViewById(R.id.tv_horaires);
        }
    }
}