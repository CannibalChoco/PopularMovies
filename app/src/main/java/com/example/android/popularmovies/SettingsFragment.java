package com.example.android.popularmovies;

/**
 * Created by Emils on 06.03.2018.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;


public class SettingsFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        int prefCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < prefCount; i++){
            Preference p = preferenceScreen.getPreference(i);
            Log.i("PREF", p.toString());
            Log.i("PREF", p.getKey());
            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.pref_sort_key))){
            // TODO: change query url
            String value = sharedPreferences.getString(key, getResources().getString(R.string.pref_sort_popularity));
            Log.i("PREF", value);
        }

        Preference preference = findPreference(key);
        if (preference != null){
            String value = sharedPreferences.getString(preference.getKey(), "");
            setPreferenceSummary(preference, value);
        }
    }


    /**
     * Helper method will be especially useful when there are different types of preferences
     *
     * @param preference the preference
     * @param value preferences keys value
     */
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        preference.setSummary(stringValue);

    }
}
