package com.example.android.userdictionary;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements EditMenu.EditMenuInterface{

    Cursor cursor;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case  R.id.context_delete:
                onContextDelete(item);
                break;
            case R.id.context_edit:
                onContextEdit(item);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void onContextEdit(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        cursor.moveToPosition(info.position);
        String val = cursor.getString(cursor.getColumnIndex("WORD"));
        EditMenu editMenu = new EditMenu();
        editMenu.show(getFragmentManager(), "Edit menu");
    }

    @Override
    public void onPositiveClick(String newWord) {
        int id = cursor.getInt(cursor.getColumnIndex("_ID"));
        String[] projection = {UserDictionary.Words._ID, UserDictionary.Words.WORD};
        String where = UserDictionary.Words._ID + "=?";
        String[] args = { Integer.toString(id) };

        ContentValues contentValues = new ContentValues();
        if(newWord != null) {
            contentValues.put(UserDictionary.Words.WORD, newWord);
            getContentResolver().update(UserDictionary.Words.CONTENT_URI, contentValues, where, args);
            Cursor c = getContentResolver().query(UserDictionary.Words.CONTENT_URI, projection, where, args, null);
            c.moveToNext();
            Toast.makeText(getApplicationContext(), "Edited : " + newWord, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Not updated.", Toast.LENGTH_SHORT).show();
        }

        updateView();
    }

    private void onContextDelete(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        cursor.moveToPosition(info.position);
        String val = cursor.getString(cursor.getColumnIndex("WORD"));
        String where = UserDictionary.Words.WORD + "=?";
        String[] args = new String[] { val };
        Log.v("MainActivity: ", "Where clause: " + where);
        getContentResolver().delete(UserDictionary.Words.CONTENT_URI, where, args);
        Toast.makeText(this, "Deleted " + val, Toast.LENGTH_SHORT).show();

        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateView();
    }

    private void updateView() {
        ContentResolver contentResolver =  getContentResolver();
        cursor = contentResolver.query(UserDictionary.Words.CONTENT_URI, null, null, null, null);
        final ListView listView = (ListView) findViewById(R.id.textView);
        try {
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    android.R.layout.two_line_list_item,
                    cursor,
                    new String[] {UserDictionary.Words.WORD, UserDictionary.Words.FREQUENCY},
                    new int[] {android.R.id.text1, android.R.id.text2},
                    0){
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    View newView = super.newView(context, cursor, parent);
                    ((TextView)newView.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                    ((TextView)newView.findViewById(android.R.id.text2)).setTextColor(Color.BLACK);
                    return newView;
                }
            };
            listView.setAdapter(simpleCursorAdapter);
            registerForContextMenu(listView);

        }finally {
            //cursor.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                onDeleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteAll() {
        getContentResolver().delete(UserDictionary.Words.CONTENT_URI, null, null);
        Toast.makeText(MainActivity.this, "All Entries deleted", Toast.LENGTH_SHORT).show();
        updateView();
    }

/*    private void onAdd() {
        Toast.makeText(MainActivity.this, "Clicked add", Toast.LENGTH_SHORT).show();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDictionary.Words.LOCALE, "en_US");
        contentValues.put(UserDictionary.Words.WORD, "abrakadabra");
        contentValues.put(UserDictionary.Words.FREQUENCY, "100");
        getContentResolver().insert(
                UserDictionary.Words.CONTENT_URI,   // the user dictionary content URI
                contentValues                          // the values to insert
        );
    }*/
}
