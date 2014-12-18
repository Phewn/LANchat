package edu.chalmers.lanchat;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by oliver on 14-12-17.
 */
public class CustomAdapter extends ArrayAdapter<ChatMessage> {
    private final Context context;

    public CustomAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        super(context, R.layout.rowlayout, chatMessages);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.rowlayout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewName = (TextView) rowView.findViewById(R.id.textViewName);
            rowView.setTag(viewHolder);

        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        float textSize = getItem(position).getTextSize();
        String name = getItem(position).getName();
        String message = getItem(position).getMessage();

        Spannable nameAndMessage = new SpannableString(name + message);
        nameAndMessage.setSpan(new ForegroundColorSpan(getItem(position).getColor()),0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.textViewName.setText(nameAndMessage);
        holder.textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);


        return rowView;
    }


    static class ViewHolder {
        public TextView textViewMessage;
        public TextView textViewName;
    }
}
