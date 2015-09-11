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

/**
 * This application demonstrates the content providers using user dictionary as an
 * example. The app lists the current words in user dictionary. Context menu options
 * are provided for editing or deleting a word from the list. Overflow menu option
 * provides a delete all feature to delete all the entries.
 * */
public class MainActivity extends ActionBarActivity implements EditMenu.EditMenuInterface{

    Cursor cursor;
    SimpleCursorAdapter simpleCursorAdapter;

    /*
    * Main entry point for this app.
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateView();
    }

    /*
    * This method queries the content provider and displays the words in user dictionary.
    * ContentResolver provides a layer of abstraction over content providers. The data in
    * content provider can be accessed by first obtaining a ContentResolver and using it to
    * query the specific content provider. The query method needs a Content URI argument to
    * specify the type of content provider that we need. This method returns a cursor which can
    * be used to load a simpleCursorAdapter to populate the list view.
    * */
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


    /*
    * onCreateContextMenu inflates a menu file and displays on top of the main activity.
    * The menu file is defined in res/menu/context_menu.xml file
    * */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }


    /*
    * onContextItemSelected method is the call back method invoked when any one of the items in
    * context menu is selected. The MenuItem parameter passed to this method can be used to get
    * details of which item was clicked.
    * */
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

    /*
    * onContextEdit is a custom method that is invoked when edit is clicked in the context menu.
    * This method uses the cursor obtained earlier to get the position of the word that was clicked.
    * Edit menu is shown as a popup. The edit menu is defined as the java class EditMenu.
    * */
    private void onContextEdit(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        cursor.moveToPosition(info.position);
        String val = cursor.getString(cursor.getColumnIndex("WORD"));
        EditMenu editMenu = new EditMenu();
        editMenu.show(getFragmentManager(), "Edit menu");
    }

    /*
    * onContextDelete is a custom method that is invoked when delete is clicked in the context menu.
    * The delete query is used on content resolver to delete the word.
    * */
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


    /*
    * onPositiveClick is a custom method that is invoked when the positive button is clicked in
    * the edit menu. The user entered string updates the original word in user dictionary.
    * */
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


    /*
    * onCreateOptionsMenu method inflates the main menu file. This file is defined in
    * res/menu/menu_main.xml
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    * onOptionsItemSelected method is invoked when an item is clicked in the overflow or options menu.
    * MenuItem parameter passed during this method's invocation can be used to get details of which item
    * was selected.
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                onDeleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * onDeleteAll method is a custom method that is invoked when delete all item is selected in
    * overflow/options menu. Thsi methos used the delete query on content resolver to delete the
    * entire user dictionary. The paramerter null specifies that all records are applicable for
    * deleting.
    * */
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
