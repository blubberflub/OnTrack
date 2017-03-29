package com.blubflub.alert.ontrack;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

public class ViewGroupUtils
{

    public static ViewGroup getParent(View view)
    {
        return (ViewGroup) view.getParent();
    }

    public static void setMargins(View view, int left, int top, int right, int bottom)
    {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
        {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static ViewGroup.LayoutParams getMargins(View view)
    {
        ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        return vlp;
    }


    public static void removeView(View view)
    {
        ViewGroup parent = getParent(view);
        if (parent != null)
        {
            parent.removeView(view);
        }
    }

    public static void moveToEnd(View goal, View time)
    {
        ViewGroup parent = getParent(goal);
        ViewGroup parent2 = getParent(time);

        if (parent == null)
        {
            return;
        }
        if (parent2 == null)
        {
            return;
        }

        removeView(goal);
        removeView(time);
        parent.addView(goal, 4);
        parent.addView(time, 5);
    }
}
