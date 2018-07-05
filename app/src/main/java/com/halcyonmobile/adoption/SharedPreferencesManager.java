package com.halcyonmobile.adoption;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_FILE_NAME = "my_app_shared_prefs";

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, 0);
    }

}
