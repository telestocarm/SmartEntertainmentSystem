package io.nandandesai.smartentertainmentsystem.utils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import io.nandandesai.smartentertainmentsystem.R;


public class ErrorDialog {
    public static void showDialog(Activity activity, String errorMessage){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        alertDialog.setTitle("Oops!");
        alertDialog.setMessage(errorMessage);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        alertDialog.show();
    }
}
