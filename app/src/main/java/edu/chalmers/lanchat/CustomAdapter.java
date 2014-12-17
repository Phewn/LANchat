package edu.chalmers.lanchat;

import android.content.Context;
import android.util.Log;
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
    private float popularityMultiple = 5;
    private float textSize;
    private float popularity;

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
            viewHolder.textViewMessage = (TextView) rowView.findViewById(R.id.textViewMessage);
            viewHolder.textViewName = (TextView) rowView.findViewById(R.id.textViewName);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textViewMessage.setText(getItem(position).getMessage());

        holder.textViewName.setText(getItem(position).getName());
        holder.textViewName.setTextColor(getItem(position).getColor());

        textSize = holder.textViewName.getTextSize();
        textSize += popularity*popularityMultiple;


        popularity = getItem(position).getPopularity();
        holder.textViewName.setTextSize(textSize);

        return rowView;
    }


    static class ViewHolder {
        public TextView textViewMessage;
        public TextView textViewName;
    }
}
