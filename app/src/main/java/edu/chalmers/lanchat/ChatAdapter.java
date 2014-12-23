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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.chalmers.lanchat.db.MessageTable;

public class ChatAdapter extends SimpleCursorAdapter {
    private static String[] from = { MessageTable.COLUMN_MESSAGE };
    private static int[] to = { android.R.id.text1 };
    private static final int layout = R.layout.rowlayout;

    private final LayoutInflater inflater;

    private float stdTextSize = 14;
    private float popMultiple = 5;

    public ChatAdapter(Context context) {
        super(context, layout, null, from, to, 0);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_NAME));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_COLOR));
        float likes = cursor.getFloat(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_LIKES));

        float textSize = (float) (stdTextSize + popMultiple * Math.log(likes));

        Spannable nameAndMessage = new SpannableString(name + message);
        nameAndMessage.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textViewNameAndMessage = (TextView) view.findViewById(R.id.textViewNameAndMessage);
        textViewNameAndMessage.setText(nameAndMessage);
        textViewNameAndMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
}
