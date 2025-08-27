package com.clinique.lasagesse.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.clinique.lasagesse.models.User;

public class SessionManager {
    private static final String PREF_NAME = "CliniqueLaSagesseSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_TYPE, user.getTypeUtilisateur().name());
        editor.putString(KEY_USER_NAME, user.getNomComplet());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserType() {
        return pref.getString(KEY_USER_TYPE, "");
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}