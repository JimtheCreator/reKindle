package utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySharedPreferences {

    private static final String PREF_NAME = "my_preferences";
    private static final String KEY_STRING_ARRAY = "string_array_key";

    public static void saveStringArray(Context context, List<String> stringArrayList) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(stringArrayList);

        editor.putString(KEY_STRING_ARRAY, json);
        editor.apply();
    }

    public static List<String> getStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString(KEY_STRING_ARRAY, null);

        if (json == null) {
            return new ArrayList<>(); // Default empty ArrayList if no value is stored
        }

        String[] stringArray = gson.fromJson(json, String[].class);
        return new ArrayList<>(Arrays.asList(stringArray));
    }

    public static void removeItem(Context context, String itemToRemove) {
        List<String> stringArrayList = getStringArray(context);
        stringArrayList.remove(itemToRemove);
        saveStringArray(context, stringArrayList);
    }


    public static void addItem(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Add or update the specified key-value pair in SharedPreferences
        editor.putString(key, value);

        // Apply the changes
        editor.apply();
    }

    public static void removeStringArray(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove the key from SharedPreferences
        editor.remove(KEY_STRING_ARRAY);

        // Apply the changes
        editor.apply();
    }
}


