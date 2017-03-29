package com.blubflub.alert.ontrack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Preferences extends PreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            final SharedPreferences.Editor ed = Main2Activity.getInstance().editSharePrefs();
            addPreferencesFromResource(R.xml.prefs);

            Preference setNotif = findPreference("notifs");
            setNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    boolean checked = Boolean.valueOf(o.toString());
                    ed.putBoolean("notif_on", checked).apply();

                    return true;
                }
            });

            Preference reset = findPreference("reset_all");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Warning");
                    builder.setMessage("Clicking the Reset button will delete all data" +
                            " stored on this application.");

                    //on ok
                    builder.setPositiveButton("Reset", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            clearPreferences();
                        }
                    });

                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    });
                    builder.show();
                    return false;
                }
            });
        }

        public void clearPreferences()
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            getActivity().getApplicationContext().deleteDatabase("data.db");

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            Main2Activity.getInstance().finish();
        }
    }
}
