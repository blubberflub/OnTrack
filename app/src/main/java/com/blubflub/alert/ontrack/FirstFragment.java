package com.blubflub.alert.ontrack;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class FirstFragment extends Fragment
{
    View view;
    LinearLayout layout1;
    LinearLayout layout2;
    private TextView[] goals;
    private TextView[] times;
    private static final String SHOWCASE_ID = "1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.frag, container, false);

        goals = new TextView[] {(TextView) view.findViewById(R.id.goal1), (TextView) view.findViewById(R.id.goal2), (TextView) view.findViewById(R.id.goal3),
                (TextView) view.findViewById(R.id.goal4), (TextView) view.findViewById(R.id.goal5), (TextView) view.findViewById(R.id.goal6)};
        times = new TextView[] {(TextView) view.findViewById(R.id.setTime1), (TextView) view.findViewById(R.id.setTime2), (TextView) view.findViewById(R.id.setTime3),
                (TextView) view.findViewById(R.id.setTime4), (TextView) view.findViewById(R.id.setTime5), (TextView) view.findViewById(R.id.setTime6)};

        //save data
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //set date
        TextView date = (TextView) view.findViewById(R.id.date);

        setDate(date);

        layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        layout2 = (LinearLayout) view.findViewById(R.id.layout2);

        //reference progress bar and text
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.prBar);
        ProgressBarAnimation progressAnimation = new ProgressBarAnimation(progressBar, 1000);
        TextView progressNum = (TextView) view.findViewById(R.id.progressNum);

        //set progress bar and text
        progressAnimation.setProgress(pref.getInt("todays_prating", 0));
        progressNum.setText(pref.getInt("todays_prating", 0) + "%");

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(Main2Activity.getInstance(), SHOWCASE_ID);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        sequence.setConfig(config);

        sequence.addSequenceItem(progressBar,
                "This is your PRating bar. Finish up all your daily tasks and you'll see this filling up quick!", "OK");

        sequence.addSequenceItem(goals[0],
                "Press the task name to view more information. Long press the task name to edit the task or use the built in timer.", "OK");


        sequence.addSequenceItem(times[3],
                "Press on any empty cards to add a new daily task.", "OK");

        sequence.start();

        for (int i = 0; i <= 5; i++)
        {
            if (pref.getBoolean(""+(i+1), false))
            {
                setGoals(pref, goals[i], times[i], i+1);
            }
        }
        return view;
    }

    public void setGoals(final SharedPreferences pref, final TextView homeGoal, final TextView setTime, final int goalNum)
    {
        //setup goal date and longclick listener
        homeGoal.setText(pref.getString("goal" + goalNum, null));
        homeGoal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Main2Activity.getInstance().goalStatsDialog(v, goalNum);
            }
        });
        homeGoal.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                registerForContextMenu(v);
                getActivity().openContextMenu(v);
                return true;
            }
        });

        //setup time and goal done color
        int time = pref.getInt("time" + goalNum, 0);

        setTime.setText(time + " Minutes");

        if (time != 0)
        {
            Main2Activity.getInstance().goalDone(homeGoal, setTime);
        } else
        {
            Main2Activity.getInstance().goalNotDone(homeGoal, setTime, goalNum);
        }

        //if goals are 4, 5 or 6

        setTime.setCompoundDrawables(null, null, null, null);

        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        setTime.setPadding(0, padding_in_px, 0, 0);

        setTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Main2Activity) getActivity()).numberPickerDialogue(homeGoal, setTime, "time" + goalNum, goalNum);
            }
        });
    }

    public void setDate(TextView text)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = sdf.format(date);

        text.setText(dateString);

        if (pref.getInt("todays_prating", 0) == 100)
        {
            text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, R.drawable.star, 0);
        }
    }

    public static FirstFragment newInstance(String text)
    {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}