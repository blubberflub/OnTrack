package com.blubflub.alert.ontrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

public class MainActivity extends AppIntro
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //  Launch app intro
        addSlide(AppIntroFragment.newInstance("Welcome", "Being productive just became a little bit easier.", R.drawable.smile,
                ContextCompat.getColor(this, R.color.Goals)));
        addSlide(AppIntroFragment.newInstance("Goals", "Keep track of your daily tasks and organize your life.", R.drawable.pencil,
                ContextCompat.getColor(this, R.color.Frag2)));
        addSlide(AppIntroFragment.newInstance("Stats", "View your daily stats and past trends.", R.drawable.stats,
                ContextCompat.getColor(this, R.color.Frag3)));

        setColorTransitionsEnabled(true);


        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        //  If the activity has never started before...
        if (!isFirstStart)
        {
            Intent next = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(next);
            finish();
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment)
    {
        super.onDonePressed(currentFragment);
        Intent next = new Intent(MainActivity.this, InputGoals.class);
        startActivity(next);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment)
    {
        super.onSkipPressed(currentFragment);
        Intent next = new Intent(MainActivity.this, InputGoals.class);
        startActivity(next);
        finish();
    }
}