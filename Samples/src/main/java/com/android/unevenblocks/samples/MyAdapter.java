package com.android.unevenblocks.samples;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yura on 07.05.14.
 */
public class MyAdapter extends BaseAdapter {
    private final MainActivity activity;
    ArrayList<String> viewsName = new ArrayList<String>();

    public MyAdapter(MainActivity activity){

        this.activity = activity;
    }
    public void AddView(String str){
        viewsName.add(str);
    }

    @Override
    public int getCount() {
        return viewsName.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView textView = new TextView(activity);
        textView.setText(viewsName.get(i));
        int color = (new Random()).nextInt();
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(getRandomColor());
        textView.setPadding(25, 40, 25, 40);
        return textView;
    }

    private int getRandomColor() {
        int random = (new Random()).nextInt(7);
        switch (random){
            case 0:
                return activity.getResources().getColor(R.color.color_0);
            case 1:
                return activity.getResources().getColor(R.color.color_2);
            case 2:
                return activity.getResources().getColor(R.color.color_3);
            case 3:
                return activity.getResources().getColor(R.color.color_4);
            case 4:
                return activity.getResources().getColor(R.color.color_5);
            case 5:
                return activity.getResources().getColor(R.color.color_6);
            case 6:
                return activity.getResources().getColor(R.color.color_7);
            case 7:
                return activity.getResources().getColor(R.color.color_1);
        }
        return Color.BLACK;
    }
}
