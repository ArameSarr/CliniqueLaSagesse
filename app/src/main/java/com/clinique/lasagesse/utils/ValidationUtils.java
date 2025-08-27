package com.clinique.lasagesse.utils;

import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return cleanPhone.length() >= 9 && Pattern.matches("^[0-9+]+$", cleanPhone);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 &&
                Pattern.matches("^[a-zA-ZÀ-ÿ\\s\\-']+$", name.trim());
    }

    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe est obligatoire";
        }
        if (password.length() < 6) {
            return "Le mot de passe doit contenir au moins 6 caractères";
        }
        return null;
    }

    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "L'email est obligatoire";
        }
        if (!isValidEmail(email)) {
            return "Format d'email invalide";
        }
        return null;
    }

    public static String getNameError(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            return fieldName + " est obligatoire";
        }
        if (!isValidName(name)) {
            return fieldName + " ne doit contenir que des lettres";
        }
        return null;
    }

    public static String getPhoneError(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Le téléphone est obligatoire";
        }
        if (!isValidPhone(phone)) {
            return "Format de téléphone invalide";
        }
        return null;
    }
}