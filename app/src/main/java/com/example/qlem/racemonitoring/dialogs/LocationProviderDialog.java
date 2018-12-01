package com.example.qlem.racemonitoring.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.qlem.racemonitoring.R;

/**
 * This class creates a pop-up window that warns the user that the device location is disabled.
 */
public class LocationProviderDialog extends DialogFragment {

    /**
     * This variable stores the listener.
     */
    private NoticeDialogListener mListener;

    /**
     * This interface defines the click listener to implement.
     */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    /**
     * Function called at the creation, initializes the dialog.
     * @param savedInstanceState the saved state
     * @return the dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(R.string.location_dialog_message)
                .setPositiveButton(R.string.location_dialog_enable, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(LocationProviderDialog.this);
                    }
                });
        return builder.create();
    }

    /**
     * Function called when the dialog is attached to an activity, initializes the listener.
     * @param context the context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NoticeDialogListener) context;
    }
}
