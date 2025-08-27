package com.clinique.lasagesse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clinique.lasagesse.R;
import com.clinique.lasagesse.models.Patient;
import com.clinique.lasagesse.utils.DateUtils;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {
    private Context context;
    private List<Patient> patientsList;
    private OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
        void onEditPatient(Patient patient);
        void onDeletePatient(Patient patient);
    }

    public PatientAdapter(Context context, List<Patient> patientsList) {
        this.context = context;
        this.patientsList = patientsList;
    }

    public void setOnPatientClickListener(OnPatientClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patientsList.get(position);

        if (patient.getUser() != null) {
            holder.tvNom.setText(patient.getUser().getNomComplet());
            holder.tvEmail.setText(patient.getUser().getEmail());
            holder.tvTelephone.setText(patient.getUser().getTelephone());
        }

        holder.tvNumeroPatient.setText(patient.getNumeroPatient());

        if (patient.getDateNaissance() != null) {
            holder.tvAge.setText(DateUtils.getAge(patient.getDateNaissance()) + " ans");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPatientClick(patient);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEditPatient(patient);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvEmail, tvTelephone, tvNumeroPatient, tvAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tv_nom);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvTelephone = itemView.findViewById(R.id.tv_telephone);
            tvNumeroPatient = itemView.findViewById(R.id.tv_numero_patient);
            tvAge = itemView.findViewById(R.id.tv_age);
        }
    }
}