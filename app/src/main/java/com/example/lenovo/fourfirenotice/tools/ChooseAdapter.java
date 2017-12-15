package com.example.lenovo.fourfirenotice.tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lenovo.fourfirenotice.R;

import java.util.List;

/**
 * Created by lenovo on 2017/12/8.
 */

public class ChooseAdapter extends ArrayAdapter<String>
{
    private int resourceId;
    public ChooseAdapter(@NonNull Context context, int resource, @NonNull List<String> objects)
    {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        String cityName = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView city = (TextView)view.findViewById(R.id.citytext);
        city.setText(cityName);
        return view;
    }
}
