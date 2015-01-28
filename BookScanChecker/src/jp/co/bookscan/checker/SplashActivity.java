package jp.co.bookscan.checker;

import android.content.Intent;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;

public class SplashActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);				
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		//DisplayMetrics dm1 = getResources().getDisplayMetrics();
		//Log.d("screen  ", "dpi:" + dm1.densityDpi + ",height:" + dm1.heightPixels + ",width:" + dm1.widthPixels);
        Handler hdlr = new Handler();
        hdlr.postDelayed(new Runnable() {
        	@Override
        	public void run() {
        		//startActivity(new Intent(SplashActivity.this, TopActivity.class));        		
        		if (checkNetwork() && checkCamera())
    			    startActivity(new Intent(SplashActivity.this.getApplicationContext(), ReaderActivity.class));
        		finish();        		
        		/* デバッグのため
        		startActivity(new Intent(SplashActivity.this.getApplicationContext(), IsbnActivity.class));
        		finish();
        		*/
        	}
        }, 800);
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
    
}