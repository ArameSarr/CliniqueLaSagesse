package com.clinique.lasagesse.utils;

import android.content.Context;
import android.os.Environment;
import com.clinique.lasagesse.models.DossierMedical;
import com.clinique.lasagesse.models.Patient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    public static String generateDossierMedicalPDF(Context context, Patient patient,
                                                   List<DossierMedical> dossiers) {
        try {
            // Créer le dossier de téléchargement s'il n'existe pas
            File downloadsDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "CliniqueLaSagesse");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            String fileName = "Dossier_Medical_" + patient.getNumeroPatient() + "_" +
                    System.currentTimeMillis() + ".pdf";
            File pdfFile = new File(downloadsDir, fileName);

            // Ici vous pouvez utiliser une bibliothèque PDF comme iText
            // Pour la simplicité, nous créons un fichier texte
            FileOutputStream fos = new FileOutputStream(pdfFile);
            StringBuilder content = new StringBuilder();

            content.append("CLINIQUE LA SAGESSE - THIÈS\n");
            content.append("================================\n\n");
            content.append("DOSSIER MÉDICAL\n\n");
            content.append("Patient: ").append(patient.getUser().getNomComplet()).append("\n");
            content.append("N° Patient: ").append(patient.getNumeroPatient()).append("\n");
            content.append("Date de naissance: ").append(DateUtils.formatDate(patient.getDateNaissance())).append("\n");
            content.append("Adresse: ").append(patient.getAdresse()).append("\n");
            content.append("Téléphone: ").append(patient.getUser().getTelephone()).append("\n\n");

            content.append("HISTORIQUE DES CONSULTATIONS\n");
            content.append("============================\n\n");

            for (DossierMedical dossier : dossiers) {
                content.append("Date: ").append(DateUtils.formatDate(dossier.getDateConsultation())).append("\n");
                content.append("Médecin: Dr. ").append(dossier.getMedecin().getUser().getNomComplet()).append("\n");
                content.append("Diagnostic: ").append(dossier.getDiagnostic()).append("\n");
                content.append("Prescription: ").append(dossier.getPrescription()).append("\n");
                content.append("Notes: ").append(dossier.getNotes()).append("\n");
                content.append("--------------------------------\n\n");
            }

            content.append("Document généré le: ").append(DateUtils.formatDateTime(new java.util.Date())).append("\n");

            fos.write(content.toString().getBytes());
            fos.close();

            return pdfFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}