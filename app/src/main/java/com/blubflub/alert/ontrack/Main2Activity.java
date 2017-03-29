package com.blubflub.alert.ontrack;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.w3c.dom.Text;

import java.util.Calendar;


public class Main2Activity extends AppCompatActivity
{
    private int tempTime;
    private int tempHours;
    private int[] timesArray = new int[7];
    private static Main2Activity ins;
    long goalId;
    DatabaseHelper db;
    SharedPreferences pref;
    SharedPreferences.Editor ed;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ins = this;

        pref = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);
        ed = pref.edit();

        //initialize
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        boolean isWorking = (PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        db = new DatabaseHelper(this);


        // Set the alarm to start every midnight
        long alarm = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis())
        {
            alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY);
        } else
        {
            alarm = calendar.getTimeInMillis();
        }

        if (!isWorking)

        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, alarm,
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }


        //Set appbar title and color
        toolbar.setTitle("OnTrack");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));


        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setCurrentItem(0);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(pager);

    }

    public SharedPreferences.Editor editSharePrefs()
    {
        return ed;
    }

    //show menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                final Intent prefActivity = new Intent(Main2Activity.this, Preferences.class);
                startActivity(prefActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Main2Activity getInstance()
    {
        return ins;
    }

    public double getPrating(SharedPreferences pref, DatabaseHelper db)
    {
        float total = 0;
        float weightTotal = 0;

        for (int i = 1; i <= 6; i++)
        {
            if ((pref.getInt("time" + i, 0) != 0))
            {
                float group = pref.getFloat("weight" + i, 0) * 100;
                total = total + group;
            }
        }

        for (int i = 1; i <= 6; i++)
        {
            float weight = pref.getFloat("weight" + i, 0);
            weightTotal = weight + weightTotal;
        }

        return (double) Math.round(total / weightTotal);
    }

    public void updateProgress()
    {
        //progress bar update and animator
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.prBar);
        ProgressBarAnimation progressAnimation = new ProgressBarAnimation(progressBar, 1000);

        //text update and animator
        final TextView progressNum = (TextView) findViewById(R.id.progressNum);
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(1000);

        //fill timesArray on app restart to properly fill progress
        for (int i = 0; i <= 5; i++)
        {
            timesArray[i] = pref.getInt("time" + (i + 1), 0);
        }

        //add up all the set times
        int dailyTotal = 0;
        for (int i : timesArray)
            dailyTotal += i;

        //save daily total minutes
        editSharePrefs().putInt("daily_total_minutes", dailyTotal);
        editSharePrefs().apply();


        //if this is the first day of using app
        if (db.getTotalMinuteAverage() == 0)
        {
            //on first run create the new average
            editSharePrefs().putInt("currentAverage", dailyTotal).apply();
        }
        //otherwise get the average daily minutes
        else
        {
            //get the average of all past pratings.
            editSharePrefs().putInt("currentAverage", db.getTotalMinuteAverage()).apply();
        }

        if (dailyTotal != 0)
        {
            //get today's prating
            double pRating = getPrating(pref, db);
            int formatted = (int) pRating;

            //set progress animations
            progressAnimation.setProgress(formatted);
            animator.setObjectValues(pref.getInt("todays_prating", 0), formatted);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    progressNum.setText("" + animation.getAnimatedValue() + "%");
                }
            });
            animator.start();

            //save daily total
            editSharePrefs().putInt("todays_prating", formatted);
            editSharePrefs().apply();
        } else
        {
            progressAnimation.setProgress(0);
            animator.setObjectValues(pref.getInt("todays_prating", 0), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    progressNum.setText("" + animation.getAnimatedValue() + "%");
                }
            });
            animator.start();
            editSharePrefs().putInt("todays_prating", 0);
            editSharePrefs().apply();
        }

        TextView date = (TextView) findViewById(R.id.date);

        if (pref.getInt("todays_prating", 0) == 100)
        {
            date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, R.drawable.star, 0);
        } else
        {
            date.setCompoundDrawables(null, null, null, null);
        }
    }

    public void deleteGoal(final View view)
    {
        TextView goal1 = (TextView) findViewById(R.id.goal1);
        TextView setTime1 = (TextView) findViewById(R.id.setTime1);
        TextView goal2 = (TextView) findViewById(R.id.goal2);
        TextView setTime2 = (TextView) findViewById(R.id.setTime2);
        TextView goal3 = (TextView) findViewById(R.id.goal3);
        TextView setTime3 = (TextView) findViewById(R.id.setTime3);
        TextView goal4 = (TextView) findViewById(R.id.goal4);
        TextView setTime4 = (TextView) findViewById(R.id.setTime4);
        TextView goal5 = (TextView) findViewById(R.id.goal5);
        TextView setTime5 = (TextView) findViewById(R.id.setTime5);
        TextView goal6 = (TextView) findViewById(R.id.goal6);
        TextView setTime6 = (TextView) findViewById(R.id.setTime6);

        switch (view.getId())
        {
            case (R.id.goal1):
                delete(goal1, setTime1, 1, "time1");
                addNewGoal(goal1, setTime1, 1, "time1");
                db.deleteAllInColumn("GOAL1");
                break;
            case (R.id.goal2):
                delete(goal2, setTime2, 2, "time2");
                setNextAddable(goal2, setTime2, 2, "time2");
                db.deleteAllInColumn("GOAL2");
                break;
            case (R.id.goal3):
                delete(goal3, setTime3, 3, "time3");
                setNextAddable(goal3, setTime3, 3, "time3");
                db.deleteAllInColumn("GOAL3");
                break;
            case (R.id.goal4):
                delete(goal4, setTime4, 4, "time4");
                setNextAddable(goal4, setTime4, 4, "time4");

                db.deleteAllInColumn("GOAL4");
                break;
            case (R.id.goal5):
                delete(goal5, setTime5, 5, "time5");
                setNextAddable(goal5, setTime5, 5, "time5");

                db.deleteAllInColumn("GOAL5");
                break;
            case (R.id.goal6):
                delete(goal6, setTime6, 6, "time6");
                setNextAddable(goal6, setTime6, 6, "time6");

                db.deleteAllInColumn("GOAL6");
                break;
        }
    }

    public void delete(final TextView goalNum, final TextView setTimeNum, final int timeNum, final String timeKey)
    {
        //remove textview date
        goalNum.setText(null);
        goalNum.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled));
        setTimeNum.setText(null);
        setTimeNum.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled));

        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        setTimeNum.setPadding(0, padding_in_px, padding_in_px, 0);

        goalNum.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                v.setHapticFeedbackEnabled(false);
                return true;
            }
        });
        goalNum.setOnClickListener(null);
        setTimeNum.setOnClickListener(null);

        editSharePrefs().putInt("time" + timeNum, 0);
        editSharePrefs().putFloat("weight" + timeNum, 0);
        editSharePrefs().putBoolean(timeNum + "", false);
        editSharePrefs().apply();

        updateProgress();
    }

    public void editGoalName(final View view)
    {
        final TextView goalName = (TextView) view;
        //open dialog and setup view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit daily task name");
        final EditText editText = new EditText(this);
        editText.setSingleLine();
        builder.setView(editText);

        //on ok
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if (editText.getText().toString().trim().length() == 0)
                {
                    Toast.makeText(Main2Activity.this, "You must enter a goal name.",
                            Toast.LENGTH_LONG).show();
                } else
                {
                    goalName.setText(editText.getText().toString());
                    editSharePrefs().putString(view.getTag().toString(), editText.getText().toString());
                    editSharePrefs().apply();
                }
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
    }

    //dialogue for making new goals
    public void addNewGoal(final TextView goalNum, final TextView setTimeNum,
                           final int timeNum, final String timeKey)
    {
        //open dialog and setup view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter a name for your new daily task.");
        final EditText editText = new EditText(this);
        editText.setSingleLine();
        builder.setView(editText);

        //on ok
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if (editText.getText().toString().trim().length() == 0)
                {
                    Toast.makeText(Main2Activity.this, "You must enter a task name.",
                            Toast.LENGTH_LONG).show();
                } else
                {
                    //get goal date from edit text
                    String goalName = editText.getText().toString();

                    //save activation and date
                    editSharePrefs().putBoolean(timeNum + "", true);
                    editSharePrefs().putString(goalNum.getTag().toString(), goalName);
                    editSharePrefs().putFloat("weight" + timeNum, 1);
                    editSharePrefs().apply();

                    //update progress
                    updateProgress();

                    //setup tile date and setTime
                    setNewTile(goalNum, setTimeNum, goalName, timeKey, timeNum);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (goalNum.getId() == R.id.goal1)
                {
                    Toast.makeText(Main2Activity.this, "Must have initial goal.", Toast.LENGTH_SHORT).show();
                    addNewGoal(goalNum, setTimeNum, timeNum, timeKey);
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if (goalNum.getId() == R.id.goal1)
                {
                    Toast.makeText(Main2Activity.this, "Must have initial goal.", Toast.LENGTH_SHORT).show();
                    addNewGoal(goalNum, setTimeNum, timeNum, timeKey);
                }
            }
        });

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        goalId = v.getId();
        MenuInflater inflater = getMenuInflater();

        if (goalId == R.id.goal1)
        {
            inflater.inflate(R.menu.hardtiles, menu);
        } else
        {
            inflater.inflate(R.menu.tile, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.edit_goal:
                editGoalName(findViewById((int) goalId));
                return true;
            case R.id.delete_goal:
                deleteGoal(findViewById((int) goalId));
                return true;
            case R.id.change_goal:
                deleteGoal(findViewById((int) goalId));
                return true;
            case R.id.timer:
                openTimer(findViewById((int) goalId));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void openTimer(View view)
    {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View main_view = inflater.inflate(R.layout.timer, null);
        dialog.setView(main_view);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        final Chronometer stopwatch = (Chronometer) main_view.findViewById(R.id.chronometer1);

        TextView startTime = (TextView) main_view.findViewById(R.id.startTime);
        TextView clearTime = (TextView) main_view.findViewById(R.id.clearTime);
        Button setTime = (Button) main_view.findViewById(R.id.setTimeButton);

        startTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopwatch.start();
                Toast.makeText(Main2Activity.this, "Timer Started", Toast.LENGTH_SHORT).show();
            }
        });

        clearTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopwatch.setBase(SystemClock.elapsedRealtime());
                stopwatch.stop();
                Toast.makeText(Main2Activity.this, "Timer Cleared", Toast.LENGTH_SHORT).show();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TextView goal1 = (TextView) findViewById(R.id.goal1);
                TextView goal2 = (TextView) findViewById(R.id.goal2);
                TextView goal3 = (TextView) findViewById(R.id.goal3);
                TextView goal4 = (TextView) findViewById(R.id.goal4);
                TextView goal5 = (TextView) findViewById(R.id.goal5);
                TextView goal6 = (TextView) findViewById(R.id.goal6);

                TextView setTime1 = (TextView) findViewById(R.id.setTime1);
                TextView setTime2 = (TextView) findViewById(R.id.setTime2);
                TextView setTime3 = (TextView) findViewById(R.id.setTime3);
                TextView setTime4 = (TextView) findViewById(R.id.setTime4);
                TextView setTime5 = (TextView) findViewById(R.id.setTime5);
                TextView setTime6 = (TextView) findViewById(R.id.setTime6);

                long minutes = ((SystemClock.elapsedRealtime() - stopwatch.getBase()) / 1000) / 60;

                Toast.makeText(Main2Activity.this, minutes + " Minutes", Toast.LENGTH_SHORT).show();

                if (goalId == R.id.goal1)
                {
                    saveTime(goal1, setTime1, 1, (int) minutes);
                }
                if (goalId == R.id.goal2)
                {
                    saveTime(goal2, setTime2, 2, (int) minutes);
                }
                if (goalId == R.id.goal3)
                {
                    saveTime(goal3, setTime3, 3, (int) minutes);
                }
                if (goalId == R.id.goal4)
                {
                    saveTime(goal4, setTime4, 4, (int) minutes);
                }
                if (goalId == R.id.goal5)
                {
                    saveTime(goal5, setTime5, 5, (int) minutes);
                }
                if (goalId == R.id.goal6)
                {
                    saveTime(goal6, setTime6, 6, (int) minutes);
                }

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void saveTime(TextView goal, TextView time, int goalNum, int minutes)
    {
        time.setText(minutes + " Minutes");
        timesArray[goalNum] = minutes;
        editSharePrefs().putInt("time" + goalNum, timesArray[goalNum]).apply();
        updateProgress();

        if (minutes != 0)
        {
            goalDone(goal, time);
        } else
        {
            goalNotDone(goal, time, goalNum);
        }
    }

    public void setNewTile(final TextView goalNum, final TextView setTimeNum, final String goalName, final String timeKey, final int timeNum)
    {
        //change goal textview name
        goalNum.setText(goalName);
        goalNum.setTextColor(ContextCompat.getColor(Main2Activity.getInstance(), R.color.Titles));
        goalNum.setBackgroundColor(ContextCompat.getColor(Main2Activity.getInstance(), android.R.color.white));
        goalNum.setOnClickListener(null);
        goalNum.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                registerForContextMenu(v);
                openContextMenu(v);
                v.setHapticFeedbackEnabled(true);
                return true;
            }
        });


        //change setText to different button
        setTimeNum.setText("0 Minutes");
        setTimeNum.setTextColor(ContextCompat.getColor(this, R.color.times));
        setTimeNum.setBackgroundResource(R.drawable.ripple);
        setTimeNum.setCompoundDrawables(null, null, null, null);

        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        setTimeNum.setPadding(0, padding_in_px, 0, 0);

        setTimeNum.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                numberPickerDialogue(goalNum, setTimeNum, timeKey, timeNum);
            }
        });
    }

    public void setTime(View view)
    {
        TextView time1 = (TextView) findViewById(R.id.setTime1);

        TextView goal1 = (TextView) findViewById(R.id.goal1);

        numberPickerDialogue(goal1, time1, "time1", 1);

    }

    public void helpDialog(View view)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Main2Activity.this);
        dialog.setView(R.layout.helpdialog);

        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    public void goalStatsDialog(View view, int goalNum)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Main2Activity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View content = inflater.inflate(R.layout.goal_stats, null);
        dialog.setView(content);

        TextView name = (TextView) content.findViewById(R.id.statsGoalName);
        TextView totalTime = (TextView) content.findViewById(R.id.totalTime);
        TextView avgTime = (TextView) content.findViewById(R.id.avgTime);

        name.setText(pref.getString("goal" + goalNum, null));
        totalTime.setText(db.getTotalOfColumn(goalNum) + " Min");
        avgTime.setText(db.getGoalMinuteAverage(goalNum) + " Min");

        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    public void createGoals(View view)
    {
        TextView goal1 = (TextView) findViewById(R.id.goal1);
        TextView goal2 = (TextView) findViewById(R.id.goal2);
        TextView goal3 = (TextView) findViewById(R.id.goal3);
        TextView goal4 = (TextView) findViewById(R.id.goal4);
        TextView goal5 = (TextView) findViewById(R.id.goal5);
        TextView goal6 = (TextView) findViewById(R.id.goal6);

        TextView setTime1 = (TextView) findViewById(R.id.setTime1);
        TextView setTime2 = (TextView) findViewById(R.id.setTime2);
        TextView setTime3 = (TextView) findViewById(R.id.setTime3);
        TextView setTime4 = (TextView) findViewById(R.id.setTime4);
        TextView setTime5 = (TextView) findViewById(R.id.setTime5);
        TextView setTime6 = (TextView) findViewById(R.id.setTime6);

        switch (view.getId())
        {
            case (R.id.goal1):
            case (R.id.setTime1):
                addNewGoal(goal1, setTime1, 1, "time1");
                break;
            case (R.id.goal2):
            case (R.id.setTime2):
                addNewGoal(goal2, setTime2, 2, "time2");
                break;
            case (R.id.goal3):
            case (R.id.setTime3):
                addNewGoal(goal3, setTime3, 3, "time3");
                break;
            case (R.id.goal4):
            case (R.id.setTime4):
                addNewGoal(goal4, setTime4, 4, "time4");
                break;
            case (R.id.goal5):
            case (R.id.setTime5):
                addNewGoal(goal5, setTime5, 5, "time5");
                break;
            case (R.id.goal6):
            case (R.id.setTime6):
                addNewGoal(goal6, setTime6, 6, "time6");
                break;
        }
    }

    public void setNextAddable(final TextView goal, final TextView setTime, final int goalNum, final String timeKey)
    {
        goal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewGoal(goal, setTime, goalNum, timeKey);
            }
        });
        setTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.add_tile, 0);
        setTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewGoal(goal, setTime, goalNum, timeKey);
            }
        });
    }

    public void numberPickerDialogue(final TextView goal, final TextView time, final String timeKey, final int goalNum)
    {
        //save data
        View npView = getLayoutInflater().inflate(R.layout.numpick_layout, null);

        //set up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(npView);
        builder.setTitle("Set time spent");

        //num pickers

        //hours
        NumberPicker hours = (NumberPicker) npView.findViewById(R.id.hours);
        final TextView hoursText = (TextView) npView.findViewById(R.id.hourText);
        hours.setMaxValue(8);
        hours.setMinValue(0);
        hours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hours.setWrapSelectorWheel(false);

        //minutes
        NumberPicker minutes = (NumberPicker) npView.findViewById(R.id.minutes);
        minutes.setMaxValue(11);
        minutes.setMinValue(0);
        minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutes.setWrapSelectorWheel(false);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter()
        {
            @Override
            public String format(int value)
            {
                int temp = value * 5;
                return temp + "";
            }
        };
        minutes.setFormatter(formatter);

        tempTime = 0;
        tempHours = 0;

        NumberPicker.OnValueChangeListener hourChange = new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                tempHours = newVal * 60;
            }
        };

        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                tempTime = newVal * 5;
            }
        };

        hours.setOnValueChangedListener(hourChange);
        minutes.setOnValueChangedListener(myValChangedListener);


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //set time and add to time array, save to prefs.
                tempTime = tempTime + tempHours;
                saveTime(goal, time, goalNum, tempTime);
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
    }

    public void goalDone(TextView goal, TextView time)
    {
        goal.setTextColor(ContextCompat.getColor(Main2Activity.getInstance(), android.R.color.white));
        goal.setBackgroundColor(ContextCompat.getColor(Main2Activity.getInstance(), R.color.Goals));
        time.setBackgroundColor(ContextCompat.getColor(Main2Activity.getInstance(), R.color.Goals));
        time.setTextColor(ContextCompat.getColor(Main2Activity.getInstance(), android.R.color.white));
    }

    public void goalNotDone(TextView goal, TextView time, int goalNum)
    {
        goal.setTextColor(ContextCompat.getColor(Main2Activity.getInstance(), R.color.Titles));
        goal.setBackgroundColor(ContextCompat.getColor(Main2Activity.getInstance(), android.R.color.white));
        time.setBackgroundResource(R.drawable.ripple);
        time.setTextColor(ContextCompat.getColor(Main2Activity.getInstance(), R.color.times));

        if (pref.getFloat("weight" + goalNum, 0) == 2)
        {
            goal.setBackgroundResource(R.color.needyButton);
            time.setBackgroundResource(R.color.needyButton);
        }
        if (pref.getFloat("weight" + goalNum, 0) >= 3)
        {
            goal.setBackgroundResource(R.color.needyButton2);
            time.setBackgroundResource(R.color.needyButton2);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider
    {

        private int tabIcons[] = {R.drawable.ic_home_black_24dp, R.drawable.uptrend};

        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int pos)
        {
            switch (pos)
            {

                case 0:
                    return FirstFragment.newInstance("home");
                case 1:
                    return SecondFragment.newInstance("graph");
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public int getPageIconResId(int position)
        {
            return tabIcons[position];
        }
    }
}
