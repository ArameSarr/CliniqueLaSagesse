package com.clinique.lasagesse.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.clinique.lasagesse.models.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper instance;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // Singleton pattern
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création table users
        String CREATE_USERS_TABLE = "CREATE TABLE " + DatabaseContract.UserEntry.TABLE_NAME + "(" +
                DatabaseContract.UserEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.UserEntry.COLUMN_NOM + " TEXT NOT NULL," +
                DatabaseContract.UserEntry.COLUMN_PRENOM + " TEXT NOT NULL," +
                DatabaseContract.UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE + " TEXT NOT NULL," +
                DatabaseContract.UserEntry.COLUMN_TELEPHONE + " TEXT," +
                DatabaseContract.UserEntry.COLUMN_TYPE_UTILISATEUR + " TEXT NOT NULL," +
                DatabaseContract.UserEntry.COLUMN_DATE_CREATION + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                DatabaseContract.UserEntry.COLUMN_ACTIF + " INTEGER DEFAULT 1" +
                ")";

        // Création table patients
        String CREATE_PATIENTS_TABLE = "CREATE TABLE " + DatabaseContract.PatientEntry.TABLE_NAME + "(" +
                DatabaseContract.PatientEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                DatabaseContract.PatientEntry.COLUMN_DATE_NAISSANCE + " DATE," +
                DatabaseContract.PatientEntry.COLUMN_ADRESSE + " TEXT," +
                DatabaseContract.PatientEntry.COLUMN_NUMERO_PATIENT + " TEXT UNIQUE," +
                "FOREIGN KEY (" + DatabaseContract.PatientEntry.COLUMN_USER_ID + ") REFERENCES " +
                DatabaseContract.UserEntry.TABLE_NAME + "(" + DatabaseContract.UserEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ")";

        // Création table medecins
        String CREATE_MEDECINS_TABLE = "CREATE TABLE " + DatabaseContract.MedecinEntry.TABLE_NAME + "(" +
                DatabaseContract.MedecinEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                DatabaseContract.MedecinEntry.COLUMN_SPECIALITE + " TEXT," +
                DatabaseContract.MedecinEntry.COLUMN_NUMERO_ORDRE + " TEXT UNIQUE," +
                DatabaseContract.MedecinEntry.COLUMN_HORAIRES_DEBUT + " TIME DEFAULT '08:00'," +
                DatabaseContract.MedecinEntry.COLUMN_HORAIRES_FIN + " TIME DEFAULT '18:00'," +
                "FOREIGN KEY (" + DatabaseContract.MedecinEntry.COLUMN_USER_ID + ") REFERENCES " +
                DatabaseContract.UserEntry.TABLE_NAME + "(" + DatabaseContract.UserEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ")";

        // Création table rendez_vous
        String CREATE_RENDEZ_VOUS_TABLE = "CREATE TABLE " + DatabaseContract.RendezVousEntry.TABLE_NAME + "(" +
                DatabaseContract.RendezVousEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID + " INTEGER NOT NULL," +
                DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + " INTEGER NOT NULL," +
                DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV + " DATE NOT NULL," +
                DatabaseContract.RendezVousEntry.COLUMN_HEURE_RDV + " TIME NOT NULL," +
                DatabaseContract.RendezVousEntry.COLUMN_MOTIF + " TEXT," +
                DatabaseContract.RendezVousEntry.COLUMN_STATUT + " TEXT DEFAULT 'EN_ATTENTE'," +
                DatabaseContract.RendezVousEntry.COLUMN_DATE_CREATION + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (" + DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID + ") REFERENCES " +
                DatabaseContract.PatientEntry.TABLE_NAME + "(" + DatabaseContract.PatientEntry.COLUMN_ID + ")," +
                "FOREIGN KEY (" + DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + ") REFERENCES " +
                DatabaseContract.MedecinEntry.TABLE_NAME + "(" + DatabaseContract.MedecinEntry.COLUMN_ID + ")" +
                ")";

        // Création table dossiers_medicaux
        String CREATE_DOSSIERS_TABLE = "CREATE TABLE " + DatabaseContract.DossierMedicalEntry.TABLE_NAME + "(" +
                DatabaseContract.DossierMedicalEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.DossierMedicalEntry.COLUMN_PATIENT_ID + " INTEGER NOT NULL," +
                DatabaseContract.DossierMedicalEntry.COLUMN_MEDECIN_ID + " INTEGER NOT NULL," +
                DatabaseContract.DossierMedicalEntry.COLUMN_DATE_CONSULTATION + " DATE NOT NULL," +
                DatabaseContract.DossierMedicalEntry.COLUMN_DIAGNOSTIC + " TEXT," +
                DatabaseContract.DossierMedicalEntry.COLUMN_PRESCRIPTION + " TEXT," +
                DatabaseContract.DossierMedicalEntry.COLUMN_NOTES + " TEXT," +
                DatabaseContract.DossierMedicalEntry.COLUMN_DATE_CREATION + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (" + DatabaseContract.DossierMedicalEntry.COLUMN_PATIENT_ID + ") REFERENCES " +
                DatabaseContract.PatientEntry.TABLE_NAME + "(" + DatabaseContract.PatientEntry.COLUMN_ID + ")," +
                "FOREIGN KEY (" + DatabaseContract.DossierMedicalEntry.COLUMN_MEDECIN_ID + ") REFERENCES " +
                DatabaseContract.MedecinEntry.TABLE_NAME + "(" + DatabaseContract.MedecinEntry.COLUMN_ID + ")" +
                ")";

        // Exécution des requêtes
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PATIENTS_TABLE);
        db.execSQL(CREATE_MEDECINS_TABLE);
        db.execSQL(CREATE_RENDEZ_VOUS_TABLE);
        db.execSQL(CREATE_DOSSIERS_TABLE);

        // Insertion de données de test
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DossierMedicalEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RendezVousEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PatientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MedecinEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserEntry.TABLE_NAME);
        onCreate(db);
    }

    private void insertDefaultData(SQLiteDatabase db) {
        try {
            // Admin par défaut
            ContentValues adminValues = new ContentValues();
            adminValues.put(DatabaseContract.UserEntry.COLUMN_NOM, "Admin");
            adminValues.put(DatabaseContract.UserEntry.COLUMN_PRENOM, "Système");
            adminValues.put(DatabaseContract.UserEntry.COLUMN_EMAIL, "admin@clinique-sagesse.sn");
            adminValues.put(DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE, "admin123");
            adminValues.put(DatabaseContract.UserEntry.COLUMN_TELEPHONE, "771234567");
            adminValues.put(DatabaseContract.UserEntry.COLUMN_TYPE_UTILISATEUR, "ADMIN");
            db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, adminValues);

            // Médecin de test
            ContentValues medecinUserValues = new ContentValues();
            medecinUserValues.put(DatabaseContract.UserEntry.COLUMN_NOM, "Diop");
            medecinUserValues.put(DatabaseContract.UserEntry.COLUMN_PRENOM, "Dr. Fatou");
            medecinUserValues.put(DatabaseContract.UserEntry.COLUMN_EMAIL, "dr.diop@clinique-sagesse.sn");
            medecinUserValues.put(DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE, "medecin123");
            medecinUserValues.put(DatabaseContract.UserEntry.COLUMN_TELEPHONE, "772345678");
            medecinUserValues.put(DatabaseContract.UserEntry.COLUMN_TYPE_UTILISATEUR, "MEDECIN");
            long medecinUserId = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, medecinUserValues);

            ContentValues medecinValues = new ContentValues();
            medecinValues.put(DatabaseContract.MedecinEntry.COLUMN_ID, medecinUserId);
            medecinValues.put(DatabaseContract.MedecinEntry.COLUMN_USER_ID, medecinUserId);
            medecinValues.put(DatabaseContract.MedecinEntry.COLUMN_SPECIALITE, "Médecine Générale");
            medecinValues.put(DatabaseContract.MedecinEntry.COLUMN_NUMERO_ORDRE, "MG001");
            medecinValues.put(DatabaseContract.MedecinEntry.COLUMN_HORAIRES_DEBUT, "08:00");
            medecinValues.put(DatabaseContract.MedecinEntry.COLUMN_HORAIRES_FIN, "18:00");
            db.insert(DatabaseContract.MedecinEntry.TABLE_NAME, null, medecinValues);

            // Patient de test
            ContentValues patientUserValues = new ContentValues();
            patientUserValues.put(DatabaseContract.UserEntry.COLUMN_NOM, "Fall");
            patientUserValues.put(DatabaseContract.UserEntry.COLUMN_PRENOM, "Aminata");
            patientUserValues.put(DatabaseContract.UserEntry.COLUMN_EMAIL, "aminata.fall@email.com");
            patientUserValues.put(DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE, "patient123");
            patientUserValues.put(DatabaseContract.UserEntry.COLUMN_TELEPHONE, "773456789");
            patientUserValues.put(DatabaseContract.UserEntry.COLUMN_TYPE_UTILISATEUR, "PATIENT");
            long patientUserId = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, patientUserValues);

            ContentValues patientValues = new ContentValues();
            patientValues.put(DatabaseContract.PatientEntry.COLUMN_ID, patientUserId);
            patientValues.put(DatabaseContract.PatientEntry.COLUMN_USER_ID, patientUserId);
            patientValues.put(DatabaseContract.PatientEntry.COLUMN_DATE_NAISSANCE, "1990-05-15");
            patientValues.put(DatabaseContract.PatientEntry.COLUMN_ADRESSE, "Dakar, Sénégal");
            patientValues.put(DatabaseContract.PatientEntry.COLUMN_NUMERO_PATIENT, "PAT001");
            db.insert(DatabaseContract.PatientEntry.TABLE_NAME, null, patientValues);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'insertion des données par défaut", e);
        }
    }

    // =============== MÉTHODES CRUD POUR USERS ===============
    public long ajouterUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.UserEntry.COLUMN_NOM, user.getNom());
        values.put(DatabaseContract.UserEntry.COLUMN_PRENOM, user.getPrenom());
        values.put(DatabaseContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE, user.getMotDePasse());
        values.put(DatabaseContract.UserEntry.COLUMN_TELEPHONE, user.getTelephone());
        values.put(DatabaseContract.UserEntry.COLUMN_TYPE_UTILISATEUR, user.getTypeUtilisateur().name());
        values.put(DatabaseContract.UserEntry.COLUMN_ACTIF, user.isActif() ? 1 : 0);

        return db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);
    }

    public User getUserByEmailAndPassword(String email, String motDePasse) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DatabaseContract.UserEntry.COLUMN_EMAIL + "=? AND " +
                DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE + "=? AND " +
                DatabaseContract.UserEntry.COLUMN_ACTIF + "=1";
        String[] selectionArgs = {email, motDePasse};

        Cursor cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DatabaseContract.UserEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, null, null, null,
                null, null, DatabaseContract.UserEntry.COLUMN_NOM + " ASC");

        if (cursor.moveToFirst()) {
            do {
                users.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.UserEntry.COLUMN_NOM, user.getNom());
        values.put(DatabaseContract.UserEntry.COLUMN_PRENOM, user.getPrenom());
        values.put(DatabaseContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseContract.UserEntry.COLUMN_TELEPHONE, user.getTelephone());
        values.put(DatabaseContract.UserEntry.COLUMN_ACTIF, user.isActif() ? 1 : 0);

        String whereClause = DatabaseContract.UserEntry.COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(user.getId())};

        return db.update(DatabaseContract.UserEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = DatabaseContract.UserEntry.COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(userId)};

        db.delete(DatabaseContract.UserEntry.TABLE_NAME, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_ID)));
        user.setNom(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NOM)));
        user.setPrenom(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_PRENOM)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EMAIL)));
        user.setMotDePasse(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_MOT_DE_PASSE)));
        user.setTelephone(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_TELEPHONE)));

        String typeStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_TYPE_UTILISATEUR));
        user.setTypeUtilisateur(User.TypeUtilisateur.valueOf(typeStr));

        user.setActif(cursor.getInt(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_ACTIF)) == 1);

        try {
            String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_DATE_CREATION));
            if (dateStr != null) {
                user.setDateCreation(dateTimeFormat.parse(dateStr));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Erreur parsing date création user", e);
        }

        return user;
    }

    // =============== MÉTHODES CRUD POUR PATIENTS ===============
    public long ajouterPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.PatientEntry.COLUMN_ID, patient.getId());
        values.put(DatabaseContract.PatientEntry.COLUMN_USER_ID, patient.getUserId());
        values.put(DatabaseContract.PatientEntry.COLUMN_ADRESSE, patient.getAdresse());
        values.put(DatabaseContract.PatientEntry.COLUMN_NUMERO_PATIENT, patient.getNumeroPatient());

        if (patient.getDateNaissance() != null) {
            values.put(DatabaseContract.PatientEntry.COLUMN_DATE_NAISSANCE,
                    dateFormat.format(patient.getDateNaissance()));
        }

        return db.insert(DatabaseContract.PatientEntry.TABLE_NAME, null, values);
    }

    public Patient getPatientByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.*, u.* FROM " + DatabaseContract.PatientEntry.TABLE_NAME + " p " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON p." +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " = u." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE p." + DatabaseContract.PatientEntry.COLUMN_USER_ID + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        Patient patient = null;
        if (cursor.moveToFirst()) {
            patient = cursorToPatient(cursor);
        }
        cursor.close();
        return patient;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.*, u.* FROM " + DatabaseContract.PatientEntry.TABLE_NAME + " p " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON p." +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " = u." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE u." + DatabaseContract.UserEntry.COLUMN_ACTIF + "=1 " +
                "ORDER BY u." + DatabaseContract.UserEntry.COLUMN_NOM + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                patients.add(cursorToPatient(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return patients;
    }

    @SuppressLint("Range")
    private Patient cursorToPatient(Cursor cursor) {
        Patient patient = new Patient();
        patient.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_ID)));
        patient.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_USER_ID)));
        patient.setAdresse(cursor.getString(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_ADRESSE)));
        patient.setNumeroPatient(cursor.getString(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_NUMERO_PATIENT)));

        try {
            String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_DATE_NAISSANCE));
            if (dateStr != null) {
                patient.setDateNaissance(dateFormat.parse(dateStr));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Erreur parsing date naissance", e);
        }

        // Ajouter les infos user
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_ID)));
        user.setNom(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NOM)));
        user.setPrenom(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_PRENOM)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EMAIL)));
        user.setTelephone(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_TELEPHONE)));

        patient.setUser(user);
        return patient;
    }

    // =============== MÉTHODES CRUD POUR MEDECINS ===============
    public long ajouterMedecin(Medecin medecin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.MedecinEntry.COLUMN_ID, medecin.getId());
        values.put(DatabaseContract.MedecinEntry.COLUMN_USER_ID, medecin.getUserId());
        values.put(DatabaseContract.MedecinEntry.COLUMN_SPECIALITE, medecin.getSpecialite());
        values.put(DatabaseContract.MedecinEntry.COLUMN_NUMERO_ORDRE, medecin.getNumeroOrdre());
        values.put(DatabaseContract.MedecinEntry.COLUMN_HORAIRES_DEBUT, medecin.getHorairesDebut());
        values.put(DatabaseContract.MedecinEntry.COLUMN_HORAIRES_FIN, medecin.getHorairesFin());

        return db.insert(DatabaseContract.MedecinEntry.TABLE_NAME, null, values);
    }

    public Medecin getMedecinByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, u.* FROM " + DatabaseContract.MedecinEntry.TABLE_NAME + " m " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON m." +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " = u." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE m." + DatabaseContract.MedecinEntry.COLUMN_USER_ID + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        Medecin medecin = null;
        if (cursor.moveToFirst()) {
            medecin = cursorToMedecin(cursor);
        }
        cursor.close();
        return medecin;
    }

    public List<Medecin> getAllMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, u.* FROM " + DatabaseContract.MedecinEntry.TABLE_NAME + " m " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " u ON m." +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " = u." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE u." + DatabaseContract.UserEntry.COLUMN_ACTIF + "=1 " +
                "ORDER BY u." + DatabaseContract.UserEntry.COLUMN_NOM + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                medecins.add(cursorToMedecin(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medecins;
    }

    @SuppressLint("Range")
    private Medecin cursorToMedecin(Cursor cursor) {
        Medecin medecin = new Medecin();
        medecin.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_ID)));
        medecin.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_USER_ID)));
        medecin.setSpecialite(cursor.getString(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_SPECIALITE)));
        medecin.setNumeroOrdre(cursor.getString(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_NUMERO_ORDRE)));
        medecin.setHorairesDebut(cursor.getString(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_HORAIRES_DEBUT)));
        medecin.setHorairesFin(cursor.getString(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_HORAIRES_FIN)));

        // Ajouter les infos user
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_ID)));
        user.setNom(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NOM)));
        user.setPrenom(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_PRENOM)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EMAIL)));
        user.setTelephone(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_TELEPHONE)));

        medecin.setUser(user);
        return medecin;
    }

    // =============== MÉTHODES CRUD POUR RENDEZ-VOUS ===============
    public long ajouterRendezVous(RendezVous rdv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID, rdv.getPatientId());
        values.put(DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID, rdv.getMedecinId());
        values.put(DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV, dateFormat.format(rdv.getDateRdv()));
        values.put(DatabaseContract.RendezVousEntry.COLUMN_HEURE_RDV, rdv.getHeureRdv());
        values.put(DatabaseContract.RendezVousEntry.COLUMN_MOTIF, rdv.getMotif());
        values.put(DatabaseContract.RendezVousEntry.COLUMN_STATUT, rdv.getStatut().name());

        return db.insert(DatabaseContract.RendezVousEntry.TABLE_NAME, null, values);
    }

    public List<RendezVous> getRendezVousByPatient(int patientId) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT rv.*, " +
                "p.*, pu.nom as patient_nom, pu.prenom as patient_prenom, " +
                "m.*, mu.nom as medecin_nom, mu.prenom as medecin_prenom " +
                "FROM " + DatabaseContract.RendezVousEntry.TABLE_NAME + " rv " +
                "INNER JOIN " + DatabaseContract.PatientEntry.TABLE_NAME + " p ON rv." +
                DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID + " = p." +
                DatabaseContract.PatientEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " pu ON p." +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " = pu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.MedecinEntry.TABLE_NAME + " m ON rv." +
                DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + " = m." +
                DatabaseContract.MedecinEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " mu ON m." +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " = mu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE rv." + DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID + "=? " +
                "ORDER BY rv." + DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(patientId)});

        if (cursor.moveToFirst()) {
            do {
                rendezVousList.add(cursorToRendezVous(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rendezVousList;
    }

    public List<RendezVous> getRendezVousByMedecin(int medecinId, String date) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT rv.*, " +
                "p.*, pu.nom as patient_nom, pu.prenom as patient_prenom, " +
                "m.*, mu.nom as medecin_nom, mu.prenom as medecin_prenom " +
                "FROM " + DatabaseContract.RendezVousEntry.TABLE_NAME + " rv " +
                "INNER JOIN " + DatabaseContract.PatientEntry.TABLE_NAME + " p ON rv." +
                DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID + " = p." +
                DatabaseContract.PatientEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " pu ON p." +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " = pu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.MedecinEntry.TABLE_NAME + " m ON rv." +
                DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + " = m." +
                DatabaseContract.MedecinEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " mu ON m." +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " = mu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE rv." + DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + "=? " +
                "AND rv." + DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV + "=? " +
                "ORDER BY rv." + DatabaseContract.RendezVousEntry.COLUMN_HEURE_RDV + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(medecinId), date});

        if (cursor.moveToFirst()) {
            do {
                rendezVousList.add(cursorToRendezVous(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rendezVousList;
    }

    public int updateStatutRendezVous(int rdvId, RendezVous.StatutRendezVous statut) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RendezVousEntry.COLUMN_STATUT, statut.name());

        String whereClause = DatabaseContract.RendezVousEntry.COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(rdvId)};

        return db.update(DatabaseContract.RendezVousEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    private RendezVous cursorToRendezVous(Cursor cursor) {
        RendezVous rdv = new RendezVous();
        rdv.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_ID)));
        rdv.setPatientId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID)));
        rdv.setMedecinId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID)));
        rdv.setHeureRdv(cursor.getString(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_HEURE_RDV)));
        rdv.setMotif(cursor.getString(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_MOTIF)));

        String statutStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_STATUT));
        rdv.setStatut(RendezVous.StatutRendezVous.valueOf(statutStr));

        try {
            String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV));
            if (dateStr != null) {
                rdv.setDateRdv(dateFormat.parse(dateStr));
            }

            String dateCreationStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.RendezVousEntry.COLUMN_DATE_CREATION));
            if (dateCreationStr != null) {
                rdv.setDateCreation(dateTimeFormat.parse(dateCreationStr));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Erreur parsing dates rendez-vous", e);
        }

        // Créer les objets Patient et Medecin avec leurs infos User
        Patient patient = new Patient();
        patient.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_ID)));

        User patientUser = new User();
        patientUser.setNom(cursor.getString(cursor.getColumnIndex("patient_nom")));
        patientUser.setPrenom(cursor.getString(cursor.getColumnIndex("patient_prenom")));
        patient.setUser(patientUser);

        Medecin medecin = new Medecin();
        medecin.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_ID)));

        User medecinUser = new User();
        medecinUser.setNom(cursor.getString(cursor.getColumnIndex("medecin_nom")));
        medecinUser.setPrenom(cursor.getString(cursor.getColumnIndex("medecin_prenom")));
        medecin.setUser(medecinUser);

        rdv.setPatient(patient);
        rdv.setMedecin(medecin);

        return rdv;
    }

    // =============== MÉTHODES CRUD POUR DOSSIERS MÉDICAUX ===============
    public long ajouterDossierMedical(DossierMedical dossier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_PATIENT_ID, dossier.getPatientId());
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_MEDECIN_ID, dossier.getMedecinId());
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_DATE_CONSULTATION,
                dateFormat.format(dossier.getDateConsultation()));
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_DIAGNOSTIC, dossier.getDiagnostic());
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_PRESCRIPTION, dossier.getPrescription());
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_NOTES, dossier.getNotes());

        return db.insert(DatabaseContract.DossierMedicalEntry.TABLE_NAME, null, values);
    }

    public List<DossierMedical> getDossiersMedicauxByPatient(int patientId) {
        List<DossierMedical> dossiers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT dm.*, " +
                "p.*, pu.nom as patient_nom, pu.prenom as patient_prenom, " +
                "m.*, mu.nom as medecin_nom, mu.prenom as medecin_prenom " +
                "FROM " + DatabaseContract.DossierMedicalEntry.TABLE_NAME + " dm " +
                "INNER JOIN " + DatabaseContract.PatientEntry.TABLE_NAME + " p ON dm." +
                DatabaseContract.DossierMedicalEntry.COLUMN_PATIENT_ID + " = p." +
                DatabaseContract.PatientEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " pu ON p." +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " = pu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.MedecinEntry.TABLE_NAME + " m ON dm." +
                DatabaseContract.DossierMedicalEntry.COLUMN_MEDECIN_ID + " = m." +
                DatabaseContract.MedecinEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " mu ON m." +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " = mu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "WHERE dm." + DatabaseContract.DossierMedicalEntry.COLUMN_PATIENT_ID + "=? " +
                "ORDER BY dm." + DatabaseContract.DossierMedicalEntry.COLUMN_DATE_CONSULTATION + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(patientId)});

        if (cursor.moveToFirst()) {
            do {
                dossiers.add(cursorToDossierMedical(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dossiers;
    }

    public int updateDossierMedical(DossierMedical dossier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_DIAGNOSTIC, dossier.getDiagnostic());
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_PRESCRIPTION, dossier.getPrescription());
        values.put(DatabaseContract.DossierMedicalEntry.COLUMN_NOTES, dossier.getNotes());

        String whereClause = DatabaseContract.DossierMedicalEntry.COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(dossier.getId())};

        return db.update(DatabaseContract.DossierMedicalEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    private DossierMedical cursorToDossierMedical(Cursor cursor) {
        DossierMedical dossier = new DossierMedical();
        dossier.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_ID)));
        dossier.setPatientId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_PATIENT_ID)));
        dossier.setMedecinId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_MEDECIN_ID)));
        dossier.setDiagnostic(cursor.getString(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_DIAGNOSTIC)));
        dossier.setPrescription(cursor.getString(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_PRESCRIPTION)));
        dossier.setNotes(cursor.getString(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_NOTES)));

        try {
            String dateStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_DATE_CONSULTATION));
            if (dateStr != null) {
                dossier.setDateConsultation(dateFormat.parse(dateStr));
            }

            String dateCreationStr = cursor.getString(cursor.getColumnIndex(DatabaseContract.DossierMedicalEntry.COLUMN_DATE_CREATION));
            if (dateCreationStr != null) {
                dossier.setDateCreation(dateTimeFormat.parse(dateCreationStr));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Erreur parsing dates dossier médical", e);
        }

        // Créer les objets Patient et Medecin
        Patient patient = new Patient();
        patient.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.PatientEntry.COLUMN_ID)));

        User patientUser = new User();
        patientUser.setNom(cursor.getString(cursor.getColumnIndex("patient_nom")));
        patientUser.setPrenom(cursor.getString(cursor.getColumnIndex("patient_prenom")));
        patient.setUser(patientUser);

        Medecin medecin = new Medecin();
        medecin.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.MedecinEntry.COLUMN_ID)));

        User medecinUser = new User();
        medecinUser.setNom(cursor.getString(cursor.getColumnIndex("medecin_nom")));
        medecinUser.setPrenom(cursor.getString(cursor.getColumnIndex("medecin_prenom")));
        medecin.setUser(medecinUser);

        dossier.setPatient(patient);
        dossier.setMedecin(medecin);

        return dossier;
    }

    // =============== MÉTHODES UTILITAIRES ===============
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DatabaseContract.UserEntry.COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    @SuppressLint("Range")
    public String generateNumeroPatient() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) as count FROM " + DatabaseContract.PatientEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();

        return String.format("PAT%03d", count + 1);
    }

    @SuppressLint("Range")
    public String generateNumeroOrdre() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) as count FROM " + DatabaseContract.MedecinEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();

        return String.format("MED%03d", count + 1);
    }

    public List<String> getCreneauxDisponibles(int medecinId, String date) {
        List<String> creneauxOccupes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + "=? AND " +
                DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV + "=? AND " +
                DatabaseContract.RendezVousEntry.COLUMN_STATUT + "!=?";
        String[] selectionArgs = {String.valueOf(medecinId), date, "ANNULE"};

        Cursor cursor = db.query(DatabaseContract.RendezVousEntry.TABLE_NAME,
                new String[]{DatabaseContract.RendezVousEntry.COLUMN_HEURE_RDV},
                selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                creneauxOccupes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Générer tous les créneaux possibles (de 8h à 18h, toutes les heures)
        List<String> tousCreneaux = new ArrayList<>();
        for (int heure = 8; heure < 18; heure++) {
            tousCreneaux.add(String.format("%02d:00", heure));
            tousCreneaux.add(String.format("%02d:30", heure));
        }

        // Retourner seulement les créneaux libres
        List<String> creneauxLibres = new ArrayList<>();
        for (String creneau : tousCreneaux) {
            if (!creneauxOccupes.contains(creneau)) {
                creneauxLibres.add(creneau);
            }
        }

        return creneauxLibres;
    }

    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> rendezVousList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT rv.*, " +
                "p.*, pu.nom as patient_nom, pu.prenom as patient_prenom, " +
                "m.*, mu.nom as medecin_nom, mu.prenom as medecin_prenom " +
                "FROM " + DatabaseContract.RendezVousEntry.TABLE_NAME + " rv " +
                "INNER JOIN " + DatabaseContract.PatientEntry.TABLE_NAME + " p ON rv." +
                DatabaseContract.RendezVousEntry.COLUMN_PATIENT_ID + " = p." +
                DatabaseContract.PatientEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " pu ON p." +
                DatabaseContract.PatientEntry.COLUMN_USER_ID + " = pu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.MedecinEntry.TABLE_NAME + " m ON rv." +
                DatabaseContract.RendezVousEntry.COLUMN_MEDECIN_ID + " = m." +
                DatabaseContract.MedecinEntry.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.UserEntry.TABLE_NAME + " mu ON m." +
                DatabaseContract.MedecinEntry.COLUMN_USER_ID + " = mu." +
                DatabaseContract.UserEntry.COLUMN_ID + " " +
                "ORDER BY rv." + DatabaseContract.RendezVousEntry.COLUMN_DATE_RDV + " DESC, " +
                "rv." + DatabaseContract.RendezVousEntry.COLUMN_HEURE_RDV + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                rendezVousList.add(cursorToRendezVous(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rendezVousList;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Activer les contraintes de clés étrangères
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}