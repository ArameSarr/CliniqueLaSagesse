package com.clinique.lasagesse.models;

import java.util.Date;

public class DossierMedical {
    private int id;
    private int patientId;
    private int medecinId;
    private Date dateConsultation;
    private String diagnostic;
    private String prescription;
    private String notes;
    private Date dateCreation;

    // Pour les jointures
    private Patient patient;
    private Medecin medecin;

    // Constructeurs
    public DossierMedical() {}

    public DossierMedical(int patientId, int medecinId, Date dateConsultation,
                          String diagnostic, String prescription, String notes) {
        this.patientId = patientId;
        this.medecinId = medecinId;
        this.dateConsultation = dateConsultation;
        this.diagnostic = diagnostic;
        this.prescription = prescription;
        this.notes = notes;
        this.dateCreation = new Date();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getMedecinId() { return medecinId; }
    public void setMedecinId(int medecinId) { this.medecinId = medecinId; }

    public Date getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(Date dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
}