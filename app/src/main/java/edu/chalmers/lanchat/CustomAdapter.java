package edu.chalmers.lanchat;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.chalmers.lanchat.db.MessageTable;


public class CustomAdapter extends SimpleCursorAdapter {
    private static final int layout = R.layout.rowlayout;
    private final Context context;

    private static String[] from = new String[] { MessageTable.COLUMN_MESSAGE };
    private static int[] to = new int[] { android.R.id.text1 };
    private final LayoutInflater inflater;

    public CustomAdapter(Context context) {
        super(context, layout, null, from, to, 0);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        TextView textViewNameAndMessage = (TextView) view.findViewById(R.id.textViewNameAndMessage);

        String message = cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_NAME));
        int popularity = cursor.getInt(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_POPULARITY));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_COLOR));

        Spannable nameAndMessage = new SpannableString(name + message);
        nameAndMessage.setSpan(new ForegroundColorSpan(color),0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewNameAndMessage.setText(message);
        float textSize = (float) (14 + 5*Math.log(popularity));
        textViewNameAndMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
}
