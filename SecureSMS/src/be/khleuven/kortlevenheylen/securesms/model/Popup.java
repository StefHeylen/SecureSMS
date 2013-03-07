package be.khleuven.kortlevenheylen.securesms.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Popup {

	public static void showPopup(String message, Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					dialog.cancel();
				}
			});
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
