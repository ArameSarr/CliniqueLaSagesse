package com.clinique.lasagesse.models;

import java.util.Date;

public class Patient {
    private int id;
    private int userId;
    private Date dateNaissance;
    private String adresse;
    private String numeroPatient;
    private User user; // Pour les jointures

    // Constructeurs
    public Patient() {}

    public Patient(int userId, Date dateNaissance, String adresse, String numeroPatient) {
        this.userId = userId;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.numeroPatient = numeroPatient;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getNumeroPatient() { return numeroPatient; }
    public void setNumeroPatient(String numeroPatient) { this.numeroPatient = numeroPatient; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}