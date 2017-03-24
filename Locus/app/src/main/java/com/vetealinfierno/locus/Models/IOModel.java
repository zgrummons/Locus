package com.vetealinfierno.locus.Models;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by zgrum on 2/27/2017.
 */

public class IOModel {

    public static final String PREFS_NAME = "LOCUS_Config";

    protected static void loadConfig (Activity context)
    {
        DBModel dbModel = DBModel.getInstance();
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        if (!settings.contains("UserId"))
            dbModel.setUserId(settings.getInt("UserId", -1));

        if (dbModel.getUserId() == -1)
            DBModel.getNewUserId();
        else
            DBModel.getGroupId();
    }

    protected static void saveConfig (Activity context)
    {
        DBModel dbModel = DBModel.getInstance();
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("UserId", dbModel.getUserId());
        editor.commit();
    }
}
