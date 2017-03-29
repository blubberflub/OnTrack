package com.blubflub.alert.ontrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InputGoals extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_goals);

        TextView logo = (TextView) findViewById(R.id.logo);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/fabfeltscript-bold.ttf");
        logo.setTypeface(type);


    }

    public void saveGoalNames()
    {


    }

    public void finishTasks(View view)
    {
        EditText taskName1 = (EditText) findViewById(R.id.inputGoal);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = pref.edit();


        if (taskName1.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please fill in three daily tasks.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ed.putString("goal1", taskName1.getText().toString());
            ed.putBoolean(1 + "", true);
            ed.putFloat("weight1", 1);
            //  Edit preference to make it false because we don't want this to run again
            ed.putBoolean("firstStart", false);
            ed.apply();

            Intent next = new Intent(InputGoals.this, Main2Activity.class);
            startActivity(next);
            finish();
        }
    }
}
