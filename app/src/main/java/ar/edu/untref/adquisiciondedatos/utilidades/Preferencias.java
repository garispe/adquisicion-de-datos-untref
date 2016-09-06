package ar.edu.untref.adquisiciondedatos.utilidades;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by garispe on 8/25/16.
 */
public class Preferencias {

    public static final String PREFERENCES = "adquisicion_de_datos_prefs";

    public static void guardarBoolean(Context context, String key, Boolean value) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void guardarString(Context context, String key, String value) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static String getString(Context context, String key) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
}
