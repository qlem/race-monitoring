package com.example.qlem.racemonitoring.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.qlem.racemonitoring.R;

/**
 * This class creates a pop-up window that provides tips about the app
 * when user performs a click on the help button.
 */
public class AppHelpDialog extends DialogFragment {

    /**
     * Function called at the creation, initializes the dialog.
     * @param savedInstanceState the saved state
     * @return the dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_help_title);
        builder.setMessage(R.string.app_help_message)
                .setPositiveButton(R.string.app_help_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
