package jp.co.bookscan.checker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;


public class SplashActivity extends Activity {	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
        Handler hdlr = new Handler();
        hdlr.postDelayed(new Runnable() {
        	@Override
        	public void run() {
        		startActivity(new Intent(SplashActivity.this, TopActivity.class));
        		finish();
        	}
        }, 800);
	}
}

