package com.example.android.userdictionary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Map;


/*
 * This class is a helper class which is instantiated when context menu is inovoked on the list view
 * in the main activity.
 */
public class EditMenu extends DialogFragment {

    EditMenuInterface editMenuInterface;

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.edit_menu, null);
        final EditText editText = ((EditText) view.findViewById(R.id.em_text));
        builder.setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newWord = editText.getText().toString();
                        editMenuInterface.onPositiveClick(newWord);
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();
    }

    /*
    * EditMenuInterface is a custom interface used to invoke onPositiveClick method in the
    * MainActivity.java class
    * */
    public interface EditMenuInterface {
        void onPositiveClick(String newWord);
    }

    /*
    * onAttach method is called when a fragment is first attached to its activity.
    * */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        editMenuInterface = (EditMenuInterface) activity;
    }
}
