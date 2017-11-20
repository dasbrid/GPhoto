package asbridged.me.uk.gphoto.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;

/**
 * Created by David on 30/11/2015.
 */
public class DeleteConfirmDialog extends DialogFragment {

    public interface DeleteDialogOKListener {
        void onDeleteDialogOK(ActionMode am);
    }

    private ActionMode actionMode;
    public void setActionMode(ActionMode am) {
        actionMode = am;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DeleteDialogOKListener activity = (DeleteDialogOKListener) getActivity();
                    activity.onDeleteDialogOK(actionMode);
                }
            });

        builder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();

    }

}
