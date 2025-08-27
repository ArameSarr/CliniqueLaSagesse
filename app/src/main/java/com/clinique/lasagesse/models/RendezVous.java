package com.clinique.lasagesse.models;

import java.util.Date;

public class RendezVous {
    private int id;
    private int patientId;
    private int medecinId;
    private Date dateRdv;
    private String heureRdv;
    private String motif;
    private StatutRendezVous statut;
    private Date dateCreation;

    // Pour les jointures
    private Patient patient;
    private Medecin medecin;

    public enum StatutRendezVous {
        EN_ATTENTE("En attente"),
        CONFIRME("Confirmé"),
        ANNULE("Annulé"),
        COMPLETE("Terminé"),
        NO_SHOW("Absent");

        private String libelle;

        StatutRendezVous(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    // Constructeurs
    public RendezVous() {}

    public RendezVous(int patientId, int medecinId, Date dateRdv,
                      String heureRdv, String motif) {
        this.patientId = patientId;
        this.medecinId = medecinId;
        this.dateRdv = dateRdv;
        this.heureRdv = heureRdv;
        this.motif = motif;
        this.statut = StatutRendezVous.EN_ATTENTE;
        this.dateCreation = new Date();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getMedecinId() { return medecinId; }
    public void setMedecinId(int medecinId) { this.medecinId = medecinId; }

    public Date getDateRdv() { return dateRdv; }
    public void setDateRdv(Date dateRdv) { this.dateRdv = dateRdv; }

    public String getHeureRdv() { return heureRdv; }
    public void setHeureRdv(String heureRdv) { this.heureRdv = heureRdv; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
}
