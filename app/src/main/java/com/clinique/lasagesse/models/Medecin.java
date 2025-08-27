package com.clinique.lasagesse.models;

public class Medecin {
    private int id;
    private int userId;
    private String specialite;
    private String numeroOrdre;
    private String horairesDebut;
    private String horairesFin;
    private User user; // Pour les jointures

    // Constructeurs
    public Medecin() {}

    public Medecin(int userId, String specialite, String numeroOrdre,
                   String horairesDebut, String horairesFin) {
        this.userId = userId;
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.horairesDebut = horairesDebut;
        this.horairesFin = horairesFin;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    public String getHorairesDebut() { return horairesDebut; }
    public void setHorairesDebut(String horairesDebut) { this.horairesDebut = horairesDebut; }

    public String getHorairesFin() { return horairesFin; }
    public void setHorairesFin(String horairesFin) { this.horairesFin = horairesFin; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
