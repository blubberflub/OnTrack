package com.blubflub.alert.ontrack;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class SecondFragment extends Fragment
{
    private List<Cards> cardList = new ArrayList<>();

    private RecyclerView recyclerView;

    private CardAdapter cardAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.frag2, container, false);
        v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.background));

        DatabaseHelper db = new DatabaseHelper(Main2Activity.getInstance());
        cardList = db.getAllUsers();
        if(cardList.size() !=0 )
        {
            TextView noCards = (TextView) v.findViewById(R.id.noCards);
            noCards.setVisibility(View.GONE);
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        cardAdapter = new CardAdapter(cardList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(v.getContext());


        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);

        return v;
    }

    public static SecondFragment newInstance(String text)
    {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}