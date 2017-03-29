package com.blubflub.alert.ontrack;

import android.widget.ProgressBar;

/**
 * Created by user on 11/24/2016.
 */

public class Cards
{
    String date;
    String prating;
    String totalTime;
    int progressBar;

    Cards(String date, String prating, String totalTime, int progressBar)
    {
        this.date = date;
        this.prating = prating;
        this.totalTime = totalTime;
        this.progressBar = progressBar;
    }

}