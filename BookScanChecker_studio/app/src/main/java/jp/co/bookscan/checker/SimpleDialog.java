package jp.co.bookscan.checker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SimpleDialog extends DialogFragment {
	
	public static SimpleDialog getNewInstance(String strTitle, String strMsg) {
		SimpleDialog sd = new SimpleDialog();
		Bundle args = new Bundle();
		args.putString("title", strTitle);
		args.putString("message", strMsg);
		sd.setArguments(args);
		return sd;
	}

	public Dialog onCreateDialog(Bundle state) {  
		AlertDialog d = new AlertDialog.Builder(getActivity())
		.setTitle(getArguments().getString("title"))
		.setMessage(getArguments().getString("message"))
		.setCancelable(true)
		.create();
		d.setCanceledOnTouchOutside(true);  
		return d;
	}  
}