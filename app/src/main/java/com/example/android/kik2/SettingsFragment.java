package com.example.android.kik2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Michal Pichowski on 2017-11-12.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
