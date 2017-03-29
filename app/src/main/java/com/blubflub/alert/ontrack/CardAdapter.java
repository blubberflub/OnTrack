package com.blubflub.alert.ontrack;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 11/24/2016.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.statsViewHolder>
{

    public static class statsViewHolder extends RecyclerView.ViewHolder
    {
        CardView cv;
        TextView dateText;
        TextView prating;
        TextView totalTimeText;
        ProgressBar progressBar;

        statsViewHolder(View itemView)
        {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            dateText = (TextView) itemView.findViewById(R.id.dateText);
            prating = (TextView) itemView.findViewById(R.id.prating);
            progressBar = (ProgressBar) itemView.findViewById(R.id.cardProgress);
            totalTimeText = (TextView) itemView.findViewById(R.id.cardTotalTime);
        }
    }

    List<Cards> stats;

    CardAdapter(List<Cards> stats)
    {
        this.stats = stats;
    }

    @Override
    public int getItemCount()
    {
        return stats.size();
    }

    @Override
    public statsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        statsViewHolder svh = new statsViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(statsViewHolder statsViewHolder, int i)
    {
        statsViewHolder.dateText.setText(stats.get(i).date);
        statsViewHolder.prating.setText(stats.get(i).prating);
        statsViewHolder.totalTimeText.setText(stats.get(i).totalTime);
        statsViewHolder.progressBar.setProgress(stats.get(i).progressBar);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}