package edu.chalmers.lanchat;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.chalmers.lanchat.db.MessageTable;

public class ChatAdapter extends SimpleCursorAdapter {
    private static String[] from = { MessageTable.COLUMN_MESSAGE };
    private static int[] to = { android.R.id.text1 };
    private static final int layout = android.R.layout.simple_list_item_2;

    private final LayoutInflater inflater;

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

        TextView nameText = (TextView) view.findViewById(android.R.id.text1);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_NAME));
        nameText.setText(name);

        TextView messageText = (TextView) view.findViewById(android.R.id.text2);
        String message = cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE));
        messageText.setText(message);
    }
}
