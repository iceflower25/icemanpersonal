package jp.co.bookscan.checker;



import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;


// acutually singleton
public class TopActivity extends FragmentActivity {

	private static BookInfoTask biTask = null;

    @Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		int id = R.layout.main;
        //for Galaxy Tab(the device with irreguler configuration)
		if (Build.MODEL.equals("SC-01C")|| Build.MODEL.equals("GT-P1000")) {
			DisplayMetrics dm = getResources().getDisplayMetrics();
			if (dm.widthPixels / dm.density < 480)
				id = R.layout.main_s;
		}

	    setContentView(id);

		String versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		setTitle(getResources().getString(R.string.app_name) + "     ver." + versionName);
	}
	
    public void onPause() {
        super.onPause();
        if (biTask != null && biTask.getStatus() != AsyncTask.Status.FINISHED) {
        	biTask.cancel(true);
        }
    }
    
	public void menuListener(View v) {
		switch(v.getId()) {
		case R.id.btn_reader:
			if (checkNetwork() && checkCamera())
			    startActivity(new Intent(TopActivity.this.getApplicationContext(), ReaderActivity.class));
			
			break;

		case R.id.btn_isbn:
			if (checkNetwork())
				showISBNDialog();
			break;

		case R.id.btn_app_info:
    		showAlertDialog(R.string.appinfo_title, R.string.appinfo_message);
		    break;
		default:
			break;
		}
	}  

	private boolean checkCamera() {
		Camera c = ReaderActivity.getCameraInstance();
		if (c == null) {
    		showAlertDialog(R.string.title_alert, R.string.camera_notfound);
			return false;
		} else {
			c.release();
			return true;
		}
	}
    private boolean checkNetwork() {
    	NetworkInfo ni = ((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    
    	if (ni == null || ni.isConnected() == false) {
    		showAlertDialog(R.string.title_alert, R.string.network_notfound);
			return false;
    	} else {
    		return true;
    	}
    }

    private void showAlertDialog(int titleId, int msgId) {
    	SimpleDialog.getNewInstance(getResources().getString(titleId),
    			getResources().getString(msgId))
    	.show(getSupportFragmentManager(), "dialog");
    }
    
	private void showISBNDialog() {
		new ISBNDialog().show(getSupportFragmentManager(), "dialog");
    }
	
	public static class ISBNDialog extends DialogFragment {  
		@Override  
		public Dialog onCreateDialog(Bundle state) {  
			LayoutInflater factory = LayoutInflater.from(getActivity());
			final View inputView = factory.inflate(R.layout.input_dialog, null);

			AlertDialog d = new AlertDialog.Builder(getActivity())
			.setTitle(R.string.isbn_title)
			.setPositiveButton(R.string.isbn_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)inputView.findViewById(R.id.dialog_edittext);
			        if (biTask != null && biTask.getStatus() != AsyncTask.Status.FINISHED) {
			        	biTask.cancel(true);
			        }
			        //old task is going to be garbage
				    biTask = new BookInfoTask(getActivity());
				    biTask.execute(et.getText().toString());

				}
			})
			.create();

			d.setView(inputView, 0, 0, 0, 0);
			d.setCanceledOnTouchOutside(true);  
			return d;
		}  
	}

}
