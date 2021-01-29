package com.example.androidphpmysql;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class CommentDialog extends AppCompatDialogFragment {

    private EditText editTextComment;

    //creating the listener for the dialog
    private CommentDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Comment")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = editTextComment.getText().toString();
                        listener.applyText(comment);
                    }
                });

        editTextComment = view.findViewById(R.id.editTextComment);

        return builder.create();
    }
//override onAttach
    public void onAttach(Context context) {
        super.onAttach(context);

        //initialising the listener
        try {
            listener = (CommentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CommentDialogListener"); //if the dialog is opened from the activity calling it but the CommentDialogListener is not implemented, then the exception will be thrown. Dialog will therefore not be opened
        }
    }
    public interface CommentDialogListener{
        void applyText(String comment);
    }
}
