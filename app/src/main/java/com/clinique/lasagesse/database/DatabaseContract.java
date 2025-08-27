package com.clinique.lasagesse.database;

public final class DatabaseContract {
    private DatabaseContract() {}

    public static final String DATABASE_NAME = "clinique_la_sagesse.db";
    public static final int DATABASE_VERSION = 1;

    // Table Users
    public static final class UserEntry {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NOM = "nom";
        public static final String COLUMN_PRENOM = "prenom";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_MOT_DE_PASSE = "mot_de_passe";
        public static final String COLUMN_TELEPHONE = "telephone";
        public static final String COLUMN_TYPE_UTILISATEUR = "type_utilisateur";
        public static final String COLUMN_DATE_CREATION = "date_creation";
        public static final String COLUMN_ACTIF = "actif";
    }

    // Table Patients
    public static final class PatientEntry {
        public static final String TABLE_NAME = "patients";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_DATE_NAISSANCE = "date_naissance";
        public static final String COLUMN_ADRESSE = "adresse";
        public static final String COLUMN_NUMERO_PATIENT = "numero_patient";
    }

    // Table Medecins
    public static final class MedecinEntry {
        public static final String TABLE_NAME = "medecins";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_SPECIALITE = "specialite";
        public static final String COLUMN_NUMERO_ORDRE = "numero_ordre";
        public static final String COLUMN_HORAIRES_DEBUT = "horaires_debut";
        public static final String COLUMN_HORAIRES_FIN = "horaires_fin";
    }

    // Table RendezVous
    public static final class RendezVousEntry {
        public static final String TABLE_NAME = "rendez_vous";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PATIENT_ID = "patient_id";
        public static final String COLUMN_MEDECIN_ID = "medecin_id";
        public static final String COLUMN_DATE_RDV = "date_rdv";
        public static final String COLUMN_HEURE_RDV = "heure_rdv";
        public static final String COLUMN_MOTIF = "motif";
        public static final String COLUMN_STATUT = "statut";
        public static final String COLUMN_DATE_CREATION = "date_creation";
    }

    // Table DossiersMedicaux
    public static final class DossierMedicalEntry {
        public static final String TABLE_NAME = "dossiers_medicaux";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PATIENT_ID = "patient_id";
        public static final String COLUMN_MEDECIN_ID = "medecin_id";
        public static final String COLUMN_DATE_CONSULTATION = "date_consultation";
        public static final String COLUMN_DIAGNOSTIC = "diagnostic";
        public static final String COLUMN_PRESCRIPTION = "prescription";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_DATE_CREATION = "date_creation";
    }
}
